name := "rawls-db-testing"

version := "0.1"

scalaVersion := "2.11.7"

mainClass in (Compile, run) := Some("org.broadinstitute.dsde.rawls.dbtest.App")

libraryDependencies ++= {
  Seq(
    "io.spray" %% "spray-json" % "1.3.1"
  )
}
