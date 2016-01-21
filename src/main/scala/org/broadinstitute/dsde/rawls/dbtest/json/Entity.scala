package org.broadinstitute.dsde.rawls.dbtest.json

case class Entity(
  name: String,
  entityType: String,
  attributes: Map[String, String],
  refs: Map[String, String]
)