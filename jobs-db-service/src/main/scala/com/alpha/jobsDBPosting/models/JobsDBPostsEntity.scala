package com.alpha.jobsDBPosting.models

import spray.json._
import scala.util._
import reactivemongo.bson.{BSONDocumentWriter, BSONDocument, BSONDocumentReader, BSONObjectID}

case class JobsDBPostsEntity(id: String = BSONObjectID.generate.stringify,
                             data: Data)

object JobsDBPostsEntity {
  implicit def toJobsDBPostsEntity(jobsDBPosts: JobsDBPosts) : JobsDBPostsEntity =
    JobsDBPostsEntity(data = jobsDBPosts.data)


  implicit object JobsDBPostsEntityBSONReader extends BSONDocumentReader[JobsDBPostsEntity] {

    def read(doc: BSONDocument): JobsDBPostsEntity =
      JobsDBPostsEntity(
        id = doc.getAs[String]("_id").get,
        data = doc.getAs[Data]("data").get
      )
  }

  implicit object JobsDBPostsEntityBSONWriter extends BSONDocumentWriter[JobsDBPostsEntity] {
    def write(jobsDBPostsEntity: JobsDBPostsEntity): BSONDocument =
      BSONDocument(
        "_id" -> jobsDBPostsEntity.id,
        "data" -> jobsDBPostsEntity.data
      )
  }
}

object JobsDBPostsEntityProtocol extends DefaultJsonProtocol {
  implicit val AttributesFormat : RootJsonFormat[Attributes] = jsonFormat8(Attributes.apply)
  implicit val DataFormat : RootJsonFormat[Data] = jsonFormat3(Data.apply)
  implicit val EntityFormat : RootJsonFormat[JobsDBPostsEntity] = jsonFormat2(JobsDBPostsEntity.apply)
}
