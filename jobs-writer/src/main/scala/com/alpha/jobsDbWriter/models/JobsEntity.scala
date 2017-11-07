package com.alpha.jobsDbWriter.models

import spray.json._
import scala.util._
import reactivemongo.bson.{BSONDocumentWriter, BSONDocument, BSONDocumentReader, BSONObjectID}

case class DataEntity(id: String = BSONObjectID.generate.stringify, attributes: Attributes, `type`: String)

object DataEntity {
  implicit def toDataEntity(data: Data) : DataEntity =
    DataEntity(attributes = data.attributes, `type` = data.`type`)


  implicit object DataEntityBSONReader extends BSONDocumentReader[DataEntity] {

    def read(doc: BSONDocument): DataEntity =
      DataEntity(
        id = doc.getAs[String]("id").get,
        attributes = doc.getAs[Attributes]("attributes").get,
        `type` = doc.getAs[String]("type").get
      )
  }

  implicit object DataEntityBSONWriter extends BSONDocumentWriter[DataEntity] {
    def write(data: DataEntity): BSONDocument =
      BSONDocument(
        "id" -> data.id,
        "type" -> data.`type`,
        "attributes" -> data.attributes
      )
  }
}


case class JobsEntity(data: DataEntity) {
  val id : String = data.id
}

object JobsEntity {
  implicit def toJobsEntity(jobs: Jobs) : JobsEntity = {
    JobsEntity(data = DataEntity.toDataEntity(jobs.data))
  }


  implicit object JobsEntityBSONReader extends BSONDocumentReader[JobsEntity] {

    def read(doc: BSONDocument): JobsEntity =
      JobsEntity(
        data = doc.getAs[DataEntity]("data").get
      )
  }

  implicit object JobsEntityBSONWriter extends BSONDocumentWriter[JobsEntity] {
    def write(jobsEntity: JobsEntity): BSONDocument =
      BSONDocument(
        "_id" -> jobsEntity.id,
        "data" -> jobsEntity.data
      )
  }
}

object JobsEntityProtocol extends DefaultJsonProtocol {
  implicit val AttributesFormat : RootJsonFormat[Attributes] = jsonFormat3(Attributes.apply)
  implicit val DataEntityFormat : RootJsonFormat[DataEntity] = jsonFormat3(DataEntity.apply)
  implicit val EntityFormat : RootJsonFormat[JobsEntity] = jsonFormat(JobsEntity.apply, "data")
}
