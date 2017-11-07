import com.github.benfradet.spark.kafka.writer._
import com.github.jasminb.jsonapi._
import com.github.jasminb.jsonapi.annotations._
import org.apache.kafka.clients.producer._
import org.apache.kafka.common.serialization._
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}
import org.mongodb.scala.bson._

import scala.annotation.meta.field


object processPosting {

  val spark: SparkSession = SparkSession
    .builder()
    .appName("Spark SQL basic example")
    .config("spark.master", "local")
    .getOrCreate()

  import spark.implicits._

  val userHome: String = System.getProperty("user.home")
  val csvString: String = s"/data/jobsdb.csv"

  val df: DataFrame = spark.read
    .option("header", "true")
    .csv(csvString)

  @Type("jobsDBPosting")
  case class JobsDBPosting(
                            @(Id@field) var id: String,
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

  val makeObjectId: UserDefinedFunction = udf { () =>
    new BsonObjectId().getValue.toString
  }

  val df_with_id: DataFrame = df
    .withColumn("id", makeObjectId())

  val ds: Dataset[JobsDBPosting] = df_with_id.as[JobsDBPosting]

  val topic = "kammy"
  val producerConfig = Map(
    "bootstrap.servers" -> "kafka:9092",
    "key.serializer" -> classOf[StringSerializer].getName,
    "value.serializer" -> classOf[ByteArraySerializer].getName
  )

  ds.writeToKafka(
    producerConfig,
    jobsDBPosting => new ProducerRecord[String, Array[Byte]](topic, converter.writeDocument(new JSONAPIDocument(jobsDBPosting)))
  )

  KafkaProducerCache.close(producerConfig)
}
