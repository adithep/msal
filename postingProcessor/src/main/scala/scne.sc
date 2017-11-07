import org.apache.spark.sql.SparkSession
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

df.show()

@Type("jobsDBPosting")
case class JobsDBPosting (
   @(Id @field) var id: String,
   address: String,
   baseSalary: String,
   datePosted: String,
   description: String,
   hiringOrganization: String,
   jobLocation: String,
   name: String,
   title: String
 ) {
  def this() = this("2", "arma", "kk", "kk", "mm", "66", "77", "88", "99")
  def getAddress: String = address
  def getBaseSalary: String = baseSalary
  def getDatePosted: String = datePosted
  def getDescription: String = description
  def getHiringOrganization: String = hiringOrganization
  def getJobLocation: String = jobLocation
  def getName: String = name
  def getTitle: String = title
}

val converter = new ResourceConverter(classOf[JobsDBPosting])

converter.enableSerializationOption(SerializationFeature.INCLUDE_RELATIONSHIP_ATTRIBUTES)

val makeObjectId = udf {() =>
  new BsonObjectId().getValue.toString
}

val df_with_id = df
  .withColumn("id", makeObjectId())

df_with_id.show()

val ds = df_with_id.as[JobsDBPosting]

ds.show()

val topic = "jobsDBPosting"
val producerConfig = Map(
 "bootstrap.servers" -> "localhost:9092",
 "key.serializer" -> classOf[StringSerializer].getName,
 "value.serializer" -> classOf[ByteArraySerializer].getName
)

ds.writeToKafka(
  producerConfig,
  jobsDBPosting => new ProducerRecord[String, Array[Byte]](topic, converter.writeDocument(new JSONAPIDocument(jobsDBPosting)))
)

KafkaProducerCache.close(producerConfig)

//val document = new JSONAPIDocument(ds.first())
//val json = converter.writeDocument(document)
//val sjosn = new String(json)
//
//val props = new Properties()
//props.put("bootstrap.servers", "kafka:9092")
//props.put("client.id", "ScalaProducerExample")
//props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
//props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
//
//val producer = new KafkaProducer[String, String](props)
//val data = new ProducerRecord[String, String](topic, sjosn)
//producer.send(data).get()
//producer.flush()
//producer.close()
