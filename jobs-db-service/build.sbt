
enablePlugins(JavaAppPackaging)

name := "jobs-db-service"

version := "1.0"

scalaVersion := "2.12.1"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaV       = "2.5.3"
  val akkaHttpV   = "10.0.9"
  val scalaTestV  = "3.0.1"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-stream" % akkaV,
    "com.typesafe.akka" %% "akka-persistence" % akkaV,
    "com.typesafe.akka" %% "akka-testkit" % akkaV,
    "com.typesafe.akka" %% "akka-http" % akkaHttpV,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpV,
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpV,
    "org.scalatest"     %% "scalatest" % scalaTestV % "test",
    "org.reactivemongo" %% "reactivemongo" % "0.12.5",
    "com.github.jasminb" % "jsonapi-converter" % "0.7",
    "org.slf4j" % "slf4j-simple" % "1.7.25",
    "ch.megard" %% "akka-http-cors" % "0.2.1"
  )
}

Revolver.settings
        