import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}
import com.github.jasminb.jsonapi._
import com.github.jasminb.jsonapi.annotations._

import scala.annotation.meta.{field, param}
import org.mongodb.scala.bson._
import org.apache.spark.sql.functions._
import com.github.benfradet.spark.kafka.writer._
import org.apache.kafka.common.serialization._
import org.apache.kafka.clients.producer._
import io.confluent.kafka.serializers.KafkaAvroSerializer
import java.util.{Date, Properties}

import com.sksamuel.avro4s.{AvroSchema, RecordFormat}
import org.apache.spark.sql.expressions.UserDefinedFunction

val spark = SparkSession
  .builder()
  .appName("Spark SQL basic example")
  .config("spark.master", "local")
  .getOrCreate()

import spark.implicits._

val userHome = System.getProperty("user.home")
val csvString = s"$userHome/repo/salary/zeplin/data/jobsdb.csv"

val df = spark.read
  .option("header", "true")
  .csv(csvString)

case class Attributes(address: String,
                      baseSalary: String,
                      datePosted: String,
                      description: String,
                      hiringOrganization: String,
                      jobLocation: String,
                      name: String,
                      title: String)

case class Data(id: String, attributes: Attributes, `type`: String = "jobsDBPosting")

case class JobsDBPosting(_id: String, data: Data)

val makeObjectId: UserDefinedFunction = udf { () =>
  new BsonObjectId().getValue.toString
}

val df_with_id: DataFrame = df
  .na.fill("")
  .withColumn("id", makeObjectId())
  .withColumn("_id", col("id"))
  .withColumn("type", lit("jobsDBPosting"))

df_with_id.show()

val ds: Dataset[JobsDBPosting] = df_with_id
  .select($"_id", struct( $"id", $"type", struct(
    $"address",
    $"baseSalary",
    $"datePosted",
    $"description",
    $"hiringOrganization",
    $"jobLocation",
    $"name",
    $"title"
  ).alias("attributes")).alias("data"))
  .as[JobsDBPosting]

ds.show()

//val format = RecordFormat[JobsDBPosting]
//val schema = AvroSchema[JobsDBPosting]
//
//val topic = "jobsDBPosting13"
//val producerConfig = Map(
//  "bootstrap.servers" -> "kafka:9092",
//  "client.id" -> "lala1",
//  "key.serializer" -> classOf[KafkaAvroSerializer].getName,
//  "value.serializer" -> classOf[KafkaAvroSerializer].getName,
//  "schema.registry.url" -> "http://kafka:8081"
//)
//
//ds.writeToKafka(
//  producerConfig,
//  jobsDBPosting => new ProducerRecord[String, Object](topic, format.to(jobsDBPosting))
//)
//
//KafkaProducerCache.close(producerConfig)
