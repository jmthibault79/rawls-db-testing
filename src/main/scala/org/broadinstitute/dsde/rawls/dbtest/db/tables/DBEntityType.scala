package org.broadinstitute.dsde.rawls.dbtest.db.tables

import slick.driver.MySQLDriver.api._

case class DBEntityType (
  id: Int,
  name: String
)

class EntityTypeTable(tag: Tag) extends Table[DBEntityType](tag, "ENTITY_TYPE") {
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  def name = column[String]("NAME")

  override def * = (id, name) <>
    (DBEntityType.tupled, DBEntityType.unapply)

  def nameIdx = index("NAME_IDX", (name), unique = true)
}
