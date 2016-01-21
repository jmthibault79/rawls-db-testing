package org.broadinstitute.dsde.rawls.dbtest.db.tables

import org.broadinstitute.dsde.rawls.dbtest.db.SlickDAO
import slick.driver.MySQLDriver.api._

case class DBEntity (
  id: Int,
  name: String,
  typeId: Int,
  workspaceId: Int
)

class EntityTable(tag: Tag) extends Table[DBEntity](tag, "ENTITY") {
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  def name = column[String]("NAME")
  def typeId = column[Int]("ENTITY_TYPE_ID")
  def workspaceId = column[Int]("WORKSPACE_ID")

  override def * = (id, name, typeId, workspaceId) <>
    (DBEntity.tupled, DBEntity.unapply)

  def entityType = foreignKey("ENTITY_TYPE_FK", typeId, SlickDAO.entityTypeQuery)(_.id)
  def workspace = foreignKey("WORKSPACE_FK", workspaceId, SlickDAO.workspaceQuery)(_.id)
  def nameTypeIdx = index("NAME_TYPE_IDX", (name, typeId), unique = true)
}
