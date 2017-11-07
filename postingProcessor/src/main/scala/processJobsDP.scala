import com.github.benfradet.spark.kafka.writer.KafkaProducerCache
import com.sksamuel.avro4s.{AvroSchema, RecordFormat}
import io.confluent.kafka.serializers.KafkaAvroSerializer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}
import org.apache.spark.sql.functions.{col, lit, struct, udf}
import org.mongodb.scala.bson.BsonObjectId
import com.github.benfradet.spark.kafka.writer._

object processJobsDP extends App {
  val spark: SparkSession = SparkSession
    .builder()
    .appName("Spark SQL basic example")
    .config("spark.master", "local")
    .getOrCreate()

  import spark.implicits._

  val csvString: String = s"/data/jobsdb.csv"

  val df: DataFrame = spark.read
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

  case class Data(id: String, attributes: Attributes, `type`: String)

  case class JobsDBPosting(_id: String, data: Data)

  val makeObjectId: UserDefinedFunction = udf { () =>
    new BsonObjectId().getValue.toString
  }

  val df_with_id: DataFrame = df
    .na.fill("")
    .withColumn("_id", col("id"))
    .withColumn("type", lit("jobsDBPosting"))

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

  val format: RecordFormat[JobsDBPosting] = RecordFormat[JobsDBPosting]

  val topic = "jobsDBPosting"
  val producerConfig = Map(
    "bootstrap.servers" -> "kafka:9092",
    "key.serializer" -> classOf[KafkaAvroSerializer].getName,
    "value.serializer" -> classOf[KafkaAvroSerializer].getName,
    "schema.registry.url" -> "http://schemaRegistry:8081"
  )

  ds.writeToKafka(
    producerConfig,
    jobsDBPosting => new ProducerRecord[String, Object](topic, format.to(jobsDBPosting))
  )

  KafkaProducerCache.close(producerConfig)


}
