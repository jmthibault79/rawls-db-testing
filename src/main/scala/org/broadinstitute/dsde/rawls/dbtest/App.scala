package org.broadinstitute.dsde.rawls.dbtest

import java.io.FileWriter

import org.broadinstitute.dsde.rawls.dbtest.db.SlickDAO
import org.broadinstitute.dsde.rawls.dbtest.ingest.Ingest
import org.broadinstitute.dsde.rawls.dbtest.generator.Generator
import org.broadinstitute.dsde.rawls.dbtest.json.Workspace

import spray.json._

import org.broadinstitute.dsde.rawls.dbtest.json.Implicits._

import scala.io.Source

object App {
  def generate(args: Seq[String]) = {
    val writer = new FileWriter(args.head)

    val intArgs = args.tail map {_.toInt}
    val generated = Generator.multiWsWithMultiLevelEntities(intArgs:_*)

    writer.write(generated.toJson.prettyPrint + System.lineSeparator)
    writer.close
  }

  def ingest(args: Seq[String]) = {
    val sourceJson = Source.fromFile(args.head).getLines.mkString
    val ws = sourceJson.parseJson.convertTo[Seq[Workspace]]
    ws foreach {
      case w =>
        Ingest.ingestWS(w)
        w.entities foreach {
          case e =>
            Ingest.ingestEntity(e, w.name)
        }
    }
  }

  def argCountCheck(args: Array[String], count: Int) = {
    val remainingArgsGiven = args.length - 1
    if (remainingArgsGiven != count) {
      println(s"$count args required: saw $remainingArgsGiven")
      System.exit(-1)
    }
  }

  def main(args : Array[String]): Unit = {
    args.headOption match {
      case Some("init") => SlickDAO.initTables
      case Some("drop") => SlickDAO.dropTables
      case Some("generate") =>
        argCountCheck(args, 5)
        generate(args.toSeq.tail)
      case Some("ingest") =>
        argCountCheck(args, 1)
        ingest(args.toSeq.tail)
      case Some(other) => println(s"Choice '$other' not recognized")
      case _ => println("Please choose init, drop, generate or ingest")
    }
  }
}
