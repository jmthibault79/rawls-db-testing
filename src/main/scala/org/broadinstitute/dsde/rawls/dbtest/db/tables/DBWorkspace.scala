package org.broadinstitute.dsde.rawls.dbtest.db.tables

import slick.driver.MySQLDriver.api._

case class DBWorkspace (
  id: Int,
  name: String
)

class WorkspaceTable(tag: Tag) extends Table[DBWorkspace](tag, "WORKSPACE") {
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  def name = column[String]("NAME", O.Length(254))

  override def * = (id, name) <>
    (DBWorkspace.tupled, DBWorkspace.unapply)

  def nameIdx = index("NAME_IDX", (name), unique = true)
}
