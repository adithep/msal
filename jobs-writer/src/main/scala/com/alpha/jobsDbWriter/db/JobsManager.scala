package com.alpha.jobsDbWriter.db

import akka.http.scaladsl.marshalling.ToResponseMarshallable
import com.alpha.jobsDbWriter.models._
import reactivemongo.api.QueryOpts
import reactivemongo.core.commands.Count

import scala.concurrent.{ExecutionContext, Future}
import reactivemongo.bson.{BSONDocument, BSONObjectID}
import reactivemongo.api.collections.bson.BSONCollection

object JobsManager {
  import MongoDB._
  import JobsEntity._

  val collection : BSONCollection = db[BSONCollection]("jobsWriter")

  def save(jobsEntity: JobsEntity)(implicit ec: ExecutionContext) : Future[Option[JobsEntity]] =
    collection.findAndUpdate(queryById(jobsEntity.id), jobsEntity, upsert = true, fetchNewObject = true).map(_.result)

  def findById(id: String)(implicit ec: ExecutionContext) : Future[Option[Jobs]] =
    collection.find(queryById(id)).one[Jobs]

  def deleteById(id: String)(implicit ec: ExecutionContext) : Future[Deleted.type] =
    collection.remove(queryById(id)).map(_ => Deleted)

  def find(implicit ec: ExecutionContext) : Future[List[BSONDocument]] =
    collection.find(emptyQuery).cursor[BSONDocument]().collect[List](20)

  private def queryById(id: String) = BSONDocument("_id" -> id)

  private def emptyQuery = BSONDocument()

}
