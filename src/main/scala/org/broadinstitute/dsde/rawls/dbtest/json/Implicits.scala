package org.broadinstitute.dsde.rawls.dbtest.json

import spray.json.DefaultJsonProtocol

object Implicits extends DefaultJsonProtocol {
  implicit val EntityFormat = jsonFormat4(Entity)
  implicit val WorkspaceFormat = jsonFormat3(Workspace)
}
