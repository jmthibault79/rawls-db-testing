package org.broadinstitute.dsde.rawls.dbtest

import java.util.regex.Pattern

import org.broadinstitute.dsde.rawls.dbtest.db.SlickDAO
import org.broadinstitute.dsde.rawls.dbtest.db.tables.{DBEntity, EntityTable}
import org.broadinstitute.dsde.rawls.dbtest.json.Workspace

import scala.concurrent.Await
import scala.concurrent.duration._

import slick.driver.MySQLDriver.api._

object Test {
  val timeout = 1.minute

  def printAllEntityTypes = {
    val query = SlickDAO.entityTypeQuery.map(_.name).result
    val types = Await.result(SlickDAO.database.run(query), timeout)
    println(s"All entity types = $types")
    types
  }

  def printAllEntitiesOfType(typeName: String) = {
    val entityNamesForType = for {
      (et, e) <- SlickDAO.entityTypeQuery join SlickDAO.entityQuery on { (et, e) => et.id === e.typeId && et.name === typeName }
    } yield e.name

    val names = Await.result(SlickDAO.database.run(entityNamesForType.result), timeout)
    println(s"All entities of type $typeName = $names")
  }

  def traverseEntityExpressionQuery(entityQuery: Query[EntityTable, DBEntity, Seq], path: Seq[String]): Query[Rep[String], String, Seq] = {
    val (nextSegment, remainingPath) = (path.head, path.tail)
    if (remainingPath.isEmpty) {
      for {
        (e, a) <- entityQuery join SlickDAO.entityAttributeQuery on { (e, a) => e.id === a.id && a.name === nextSegment }
      } yield a.value
    }
    else {
      val nextEntityQuery = SlickDAO.entityQueryByReference(entityQuery, nextSegment)
      traverseEntityExpressionQuery(nextEntityQuery, remainingPath)
    }
  }

  /**
   * Evaluate a simplified entity expression, traversing a reference path
   *
   * @param entityName Name of the entity
   * @param entityType Name of the entity type
   * @param expression An entity expression with the format '(reference.)*attribute', e.g.
   *                   'source' -> the value of the 'source' attribute in the current entity
   *                   'child1.child3.color' -> traverse reference 'child1' from current entity, then reference
   *                   'child3' from that entity, and return the value of the 'color' attribute in the third entity
   */
  def entityExpressionQuery(entityName: String, entityType: String, expression: String) = {
    val initialEntityQuery = SlickDAO.entityQueryByNameAndType(entityName, entityType)
    val path: Seq[String] = expression split Pattern.quote(".")

    val attrVal = traverseEntityExpressionQuery(initialEntityQuery, path)

    val result = Await.result(SlickDAO.database.run(attrVal.result), timeout)
    println(s"Expression $expression on entity $entityName of type $entityType = $result")
  }

  def typeTests(ws: Seq[Workspace]) = {
    val entityTypes = printAllEntityTypes
    entityTypes foreach printAllEntitiesOfType
  }

  def exprTests(ws: Seq[Workspace]) = {
    // requires a generated JSON with at least 3 entity levels and at least 1 attribute
    // a successful test will return the same result for all
    entityExpressionQuery("WS1_1_1_1", "Level 3 Entity", "UUID 1")
    entityExpressionQuery("WS1_1_1", "Level 2 Entity", "child_1.UUID 1")
    entityExpressionQuery("WS1_1", "Level 1 Entity", "child_1.child_1.UUID 1")
  }

}
