package com.alpha.jobsDBPosting.models

import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

case class Attributes(address: String,
                      baseSalary: String,
                      datePosted: String,
                      description: String,
                      hiringOrganization: String,
                      jobLocation: String,
                      name: String,
                      title: String)

object Attributes {


  implicit object AttributesBSONReader extends BSONDocumentReader[Attributes] {

    def read(doc: BSONDocument): Attributes =
      Attributes(
        address = doc.getAs[String]("address").get,
        baseSalary = doc.getAs[String]("baseSalary").get,
        datePosted = doc.getAs[String]("datePosted").get,
        description = doc.getAs[String]("description").get,
        hiringOrganization = doc.getAs[String]("hiringOrganization").get,
        jobLocation = doc.getAs[String]("jobLocation").get,
        name = doc.getAs[String]("name").get,
        title = doc.getAs[String]("title").get
      )
  }

  implicit object AttributesBSONWriter extends BSONDocumentWriter[Attributes] {
    def write(attributes: Attributes): BSONDocument =
      BSONDocument(
        "address" -> attributes.address,
        "baseSalary" -> attributes.baseSalary,
        "datePosted" -> attributes.datePosted,
        "description" -> attributes.description,
        "hiringOrganization" -> attributes.hiringOrganization,
        "jobLocation" -> attributes.jobLocation,
        "name" -> attributes.name,
        "title" -> attributes.title
      )
  }
}

case class Data(id: String, attributes: Attributes, `type`: String)

object Data {
  implicit def toData(data: Data) : Data =
    Data(id = data.id, attributes = data.attributes, `type` = data.`type`)


  implicit object DataBSONReader extends BSONDocumentReader[Data] {

    def read(doc: BSONDocument): Data =
      Data(
        id = doc.getAs[String]("id").get,
        attributes = doc.getAs[Attributes]("attributes").get,
        `type` = doc.getAs[String]("type").get
      )
  }

  implicit object DataBSONWriter extends BSONDocumentWriter[Data] {
    def write(data: Data): BSONDocument =
      BSONDocument(
        "id" -> data.id,
        "type" -> data.`type`,
        "attributes" -> data.attributes
      )
  }
}

case class JobsDBPosts(data: Data)

object JobsDBPosts {


  implicit object JobsDBPostsBSONReader extends BSONDocumentReader[JobsDBPosts] {

    def read(doc: BSONDocument): JobsDBPosts =
      JobsDBPosts(
        data = doc.getAs[Data]("data").get
      )
  }

  implicit object JobsDBPostsBSONWriter extends BSONDocumentWriter[JobsDBPosts] {
    def write(jobsDBPosts: JobsDBPosts): BSONDocument =
      BSONDocument(
        "data" -> jobsDBPosts.data
      )
  }
}


object JobsDBPostsProtocol extends DefaultJsonProtocol{
  implicit val AttributesFormat : RootJsonFormat[Attributes] = jsonFormat8(Attributes.apply)
  implicit val DataFormat : RootJsonFormat[Data] = jsonFormat3(Data.apply)
  implicit val JobsDBPostsFormat : RootJsonFormat[JobsDBPosts] = jsonFormat1(JobsDBPosts.apply)

}

case class JobsDBPostsLists(data: List[Data])

object JobsDBPostsLists {


  implicit object JobsDBPostsListsBSONReader extends BSONDocumentReader[JobsDBPostsLists] {

    def read(doc: BSONDocument): JobsDBPostsLists =
      JobsDBPostsLists(
        data = doc.getAs[List[Data]]("data").get
      )
  }

  implicit object JobsDBPostsListsBSONWriter extends BSONDocumentWriter[JobsDBPostsLists] {
    def write(jobsDBPosts: JobsDBPostsLists): BSONDocument =
      BSONDocument(
        "data" -> jobsDBPosts.data
      )
  }
}


object JobsDBPostsListsProtocol extends DefaultJsonProtocol{
  implicit val AttributesFormat : RootJsonFormat[Attributes] = jsonFormat8(Attributes.apply)
  implicit val DataFormat : RootJsonFormat[Data] = jsonFormat3(Data.apply)
  implicit val JobsDBPostsListsFormat : RootJsonFormat[JobsDBPostsLists] = jsonFormat1(JobsDBPostsLists.apply)

}
