name := "rawls-db-testing"

version := "0.1"

scalaVersion := "2.11.7"

mainClass in (Compile, run) := Some("org.broadinstitute.dsde.rawls.dbtest.App")

libraryDependencies ++= {
  val slickV = "3.1.0"

  Seq(
    "io.spray" %% "spray-json" % "1.3.1",
    "com.typesafe.slick" %% "slick" % slickV,

    // Slick claims to need this, disables logging
    "org.slf4j" % "slf4j-nop" % "1.6.4",

    // not Scala-version-specific.  Requires a single %
    "mysql" % "mysql-connector-java" % "5.1.38"
  )
}
