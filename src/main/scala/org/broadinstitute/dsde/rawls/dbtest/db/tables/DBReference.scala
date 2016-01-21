package org.broadinstitute.dsde.rawls.dbtest.db.tables

import org.broadinstitute.dsde.rawls.dbtest.db.SlickDAO
import slick.driver.MySQLDriver.api._

case class DBReference (
  parentId: Int,
  childId: Int,
  name: String
)

class ReferenceTable(tag: Tag) extends Table[DBReference](tag, "REFERENCE") {
  def parentId = column[Int]("PARENT_ID")
  def childId = column[Int]("CHILD_ID")
  def name = column[String]("NAME")

  override def * = (parentId, childId, name) <>
    (DBReference.tupled, DBReference.unapply)

  def parent = foreignKey("PARENT_FK", parentId, SlickDAO.entityQuery)(_.id)
  def child = foreignKey("CHILD_FK", childId, SlickDAO.entityQuery)(_.id)
  def nameIdx = index("PARENT_CHILD_IDX", (parentId, childId), unique = true)
}
