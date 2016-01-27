package org.broadinstitute.dsde.rawls.dbtest.db.tables

import org.broadinstitute.dsde.rawls.dbtest.db.SlickDAO
import slick.driver.MySQLDriver.api._

case class DBAttribute (
  id: Int,
  name: String,
  value: String
)

class EntityAttributeTable(tag: Tag) extends Table[DBAttribute](tag, "ENTITY_ATTRIBUTE") {
  def id = column[Int]("ID")  // refers to an entity ID
  def name = column[String]("NAME")
  def value = column[String]("VALUE")

  override def * = (id, name, value) <>
    (DBAttribute.tupled, DBAttribute.unapply)

  def entity = foreignKey("ENTITY_ATTR_FK", id, SlickDAO.entityQuery)(_.id)
}

class WorkspaceAttributeTable(tag: Tag) extends Table[DBAttribute](tag, "WORKSPACE_ATTRIBUTE") {
  def id = column[Int]("ID")  // refers to a workspace ID
  def name = column[String]("NAME")
  def value = column[String]("VALUE")

  override def * = (id, name, value) <>
    (DBAttribute.tupled, DBAttribute.unapply)

  def workspace = foreignKey("WORKSPACE_ATTR_FK", id, SlickDAO.workspaceQuery)(_.id)
}
