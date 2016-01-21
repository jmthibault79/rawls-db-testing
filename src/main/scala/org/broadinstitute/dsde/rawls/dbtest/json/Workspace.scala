package org.broadinstitute.dsde.rawls.dbtest.json

case class Workspace(
  name: String,
  attributes: Map[String, String],
  entities: Seq[Entity]
)
