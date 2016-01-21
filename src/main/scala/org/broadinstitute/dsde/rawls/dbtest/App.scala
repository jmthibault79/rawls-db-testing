package org.broadinstitute.dsde.rawls.dbtest

import java.io.FileWriter

import org.broadinstitute.dsde.rawls.dbtest.generator.Generator

import spray.json._

import org.broadinstitute.dsde.rawls.dbtest.json.Implicits._

object App {
  def generate(args: Seq[String]) = {
    val writer = new FileWriter(args.head)

    val intArgs = args.tail map {_.toInt}
    val generated = Generator.multiWsWithMultiLevelEntities(intArgs:_*)

    writer.write(generated.toJson.prettyPrint + System.lineSeparator)
    writer.close
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
      case Some("generate") =>
        argCountCheck(args, 5)
        generate(args.toSeq.tail)
      case Some(other) => println(s"Choice '$other' not recognized")
      case _ => println("Please choose generate")
    }
  }
}
