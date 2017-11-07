package com.alpha.jobsDbWriter.models

import com.alpha.jobsDbWriter.models.JobsProtocol.jsonFormat3
import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

case class Attributes(company: Option[String],
                      position: Option[String],
                      salary: Option[String])

object Attributes {

  implicit def string2Option(s: String) : Some[String] = Some(s)

  implicit object AttributesBSONReader extends BSONDocumentReader[Attributes] {

    def read(doc: BSONDocument): Attributes =
      Attributes(
        company = doc.getAs[String]("company"),
        position = doc.getAs[String]("position"),
        salary = doc.getAs[String]("salary")
      )
  }

  implicit object AttributesBSONWriter extends BSONDocumentWriter[Attributes] {
    def write(attributes: Attributes): BSONDocument =
      BSONDocument(
        "company" -> attributes.company.getOrElse(""),
        "position" -> attributes.position.getOrElse(""),
        "salary" -> attributes.salary.getOrElse("")
      )
  }
}

case class Data(id: Option[String], attributes: Attributes, `type`: String)

object Data {

  implicit def string2Option(s: String) : Some[String] = Some(s)

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

case class Jobs(data: Data)


object Jobs {


  implicit object JobsBSONReader extends BSONDocumentReader[Jobs] {

    def read(doc: BSONDocument): Jobs =
      Jobs(
        data = doc.getAs[Data]("data").get
      )
  }

  implicit object JobsBSONWriter extends BSONDocumentWriter[Jobs] {
    def write(jobsDBPosts: Jobs): BSONDocument =
      BSONDocument(
        "data" -> jobsDBPosts.data
      )
  }
}


object JobsProtocol extends DefaultJsonProtocol{
  implicit val AttributesFormat : RootJsonFormat[Attributes] = jsonFormat3(Attributes.apply)
  implicit val DataFormat : RootJsonFormat[Data] = jsonFormat3(Data.apply)
  implicit val JobsFormat : RootJsonFormat[Jobs] = jsonFormat1(Jobs.apply)

}

case class JobsLists(data: List[Data])


object JobsListsProtocol extends DefaultJsonProtocol{
  implicit val AttributesFormat : RootJsonFormat[Attributes] = jsonFormat3(Attributes.apply)
  implicit val DataFormat : RootJsonFormat[Data] = jsonFormat3(Data.apply)
  implicit val JobsListsFormat : RootJsonFormat[JobsLists] = jsonFormat1(JobsLists.apply)

}
