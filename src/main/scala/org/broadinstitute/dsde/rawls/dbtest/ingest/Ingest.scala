package org.broadinstitute.dsde.rawls.dbtest.ingest

import org.broadinstitute.dsde.rawls.dbtest.db.SlickDAO
import org.broadinstitute.dsde.rawls.dbtest.json.{Entity, Workspace}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{Try, Failure, Success}

object Ingest {
  val debug = false
  def ingestWS(workspace: Workspace) = {
    val dbFuture = SlickDAO.insertWorkspace(workspace)
    Try { Await.result(dbFuture, 1.minute) } match {
      case Success(s) => if (debug) println(s"Success! $s")
      case Failure(f) => println(s"Failure! $f")
    }
  }

  def ingestEntity(entity: Entity, workspaceName: String) = {
    val dbFuture = SlickDAO.insertEntity(entity, workspaceName)
    Try { Await.result(dbFuture, 1.minute) } match {
      case Success(s) => if (debug) println(s"Success! $s")
      case Failure(f) => println(s"Failure! $f")
    }
  }

}
