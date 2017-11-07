name := "postingProcessor"

version := "1.0"

scalaVersion := "2.11.11"

resolvers ++= Seq(
  "confluent" at "http://packages.confluent.io/maven/",
  Resolver.mavenLocal //so we can use local build of kafka-avro-serializer
)

libraryDependencies ++= Seq(
  "com.github.jasminb" % "jsonapi-converter" % "0.7",
  "org.scalamacros" % "paradise_2.11.11" % "2.1.1",
  "org.mongodb.scala" % "mongo-scala-bson_2.11" % "2.1.0",
  "org.apache.spark" %% "spark-sql" % "2.2.0",
  "org.apache.spark" %% "spark-streaming" % "2.2.0",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.6.5",
  "com.github.benfradet" %% "spark-kafka-writer" % "0.4.0",
  "com.sksamuel.avro4s" %% "avro4s-core" % "1.7.0",
  "io.confluent" % "kafka-avro-serializer" % "3.3.0"
)

dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-databind" % "2.6.5"
dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-annotations" % "2.6.5"
dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-core" % "2.6.5"
