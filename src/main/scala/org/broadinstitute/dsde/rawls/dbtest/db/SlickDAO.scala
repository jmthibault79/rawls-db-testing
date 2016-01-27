package org.broadinstitute.dsde.rawls.dbtest.db

import java.io.File
import com.typesafe.config.ConfigFactory
import org.broadinstitute.dsde.rawls.dbtest.json.{Workspace, Entity}

import slick.driver.MySQLDriver.api._

import org.broadinstitute.dsde.rawls.dbtest.db.tables._

import scala.concurrent.{Future, Await}
import scala.concurrent.duration._

import scala.concurrent.ExecutionContext.Implicits.global

object SlickDAO {
  val conf = ConfigFactory.parseFile(new File("application.conf"))
  val database = Database.forConfig("db", conf)

  def entityQuery = TableQuery[EntityTable]
  def entityTypeQuery = TableQuery[EntityTypeTable]
  def referenceQuery = TableQuery[ReferenceTable]
  def workspaceQuery = TableQuery[WorkspaceTable]
  def entityAttributeQuery = TableQuery[EntityAttributeTable]
  def workspaceAttributeQuery = TableQuery[WorkspaceAttributeTable]

  val schema =
    entityQuery.schema ++
    entityTypeQuery.schema ++
    referenceQuery.schema ++
    workspaceQuery.schema ++
    entityAttributeQuery.schema ++
    workspaceAttributeQuery.schema


  def initTables = Await.result(database.run(DBIO.seq(schema.create)), 30.seconds)
  def dropTables = Await.result(database.run(DBIO.seq(schema.drop)), 30.seconds)

  // TODO: some scala magic for autoinc boilerplate

  val autoIncrementDummyValue = -1

  def AutoIncrementingWorkspace(name: String) =
    DBWorkspace(SlickDAO.autoIncrementDummyValue, name)

  def AutoIncrementingDBEntity(name: String, typeId: Int, workspaceId: Int) =
    DBEntity(SlickDAO.autoIncrementDummyValue, name, typeId, workspaceId)

  def AutoIncrementingDBEntityType(name: String) =
    DBEntityType(SlickDAO.autoIncrementDummyValue, name)

  // returns workspace along with auto-incremented ID
  def workspaceQueryWithId =
    (workspaceQuery returning workspaceQuery.map(_.id)
      into ((workspace, id) => workspace.copy(id = id)))

  // returns entity type along with auto-incremented ID
  def entityQueryWithId =
    (entityQuery returning entityQuery.map(_.id)
      into ((entity, id) => entity.copy(id = id)))

  // returns entity type along with auto-incremented ID
  def entityTypeQueryWithId =
    (entityTypeQuery returning entityTypeQuery.map(_.id)
      into ((entityType, id) => entityType.copy(id = id)))

  // returns a specific entity when given name and type
  def entityQueryByNameAndType(entityName: String, entityType: String) = for {
    (et, e) <- entityTypeQuery join entityQuery on {
      (et, e) => et.id === e.typeId && e.name === entityName && et.name === entityType
    }
  } yield e

  // returns an entity referenced by a parent entity
  def entityQueryByReference(parentEntity: Query[EntityTable, DBEntity, Seq], referenceName: String) = for {
    ((e1, r), e2) <- parentEntity join referenceQuery join entityQuery on {
      case ((e1, r), e2) => e1.id === r.parentId && e2.id === r.childId && r.name === referenceName
    }
  } yield e2

  // TODO: more idiomatic Slick to eliminate DB round-trips?
  def insertWorkspace(workspace: Workspace) = {
    val inserter = workspaceQueryWithId += AutoIncrementingWorkspace(workspace.name)
    database.run(inserter).map(_.id) flatMap { workspaceId =>
      Future.sequence {
        workspace.attributes.map { case (key, value) =>
          val attrInserter = workspaceAttributeQuery += DBAttribute(workspaceId, key, value)
          database.run(attrInserter)
        }
      }
    }
  }

  // TODO: more idiomatic Slick to eliminate DB round-trips?
  def insertEntityTypeIfRequired(name: String): Future[Int] = {
    val query = entityTypeQuery.filter(_.name === name).map(_.id).result
    database.run(query) flatMap { id =>
      id.headOption match {
        case Some(x: Int) => Future.successful(x)
        case None =>
          val inserter = entityTypeQueryWithId += AutoIncrementingDBEntityType(name)
          database.run(inserter).map(_.id)
      }
    }
  }

  // TODO: more idiomatic Slick to eliminate DB round-trips?
  def insertEntity(entity: Entity, workspace: String) = {
    insertEntityTypeIfRequired(entity.entityType) flatMap { entityTypeId =>
      val query = workspaceQuery.filter(_.name === workspace).map(_.id).result
      database.run(query) flatMap { workspaceId =>
        val inserter = entityQueryWithId += AutoIncrementingDBEntity(entity.name, entityTypeId, workspaceId.head)
        database.run(inserter).map(_.id) flatMap { parentEntityId =>
          Future.sequence {
            entity.refs.map { case (refName, childEntity) =>
              val childQuery = entityQuery.filter(_.name === childEntity).map(_.id).result.head
              database.run(childQuery) flatMap { childEntityId =>
                val refInserter = referenceQuery += DBReference(parentEntityId, childEntityId, refName)
                database.run(refInserter)
              }
            } ++
              entity.attributes.map { case (key, value) =>
                val attrInserter = entityAttributeQuery += DBAttribute(parentEntityId, key, value)
                database.run(attrInserter)
              }
          }
        }
      }
    }
  }

}
