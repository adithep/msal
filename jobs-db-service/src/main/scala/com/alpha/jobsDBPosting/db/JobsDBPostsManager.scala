package com.alpha.jobsDBPosting.db

import akka.http.scaladsl.marshalling.ToResponseMarshallable
import com.alpha.jobsDBPosting.models._
import reactivemongo.api.QueryOpts
import reactivemongo.core.commands.Count

import scala.concurrent.{ExecutionContext, Future}
import reactivemongo.bson.{BSONDocument, BSONObjectID}
import reactivemongo.api.collections.bson.BSONCollection

object JobsDBPostsManager {
  import MongoDB._
  import JobsDBPostsEntity._

  val collection : BSONCollection = db[BSONCollection]("jobsDBPosting")

  def save(jobsDBPostsEntity: JobsDBPostsEntity)(implicit ec: ExecutionContext) : Future[Created] =
    collection.insert(jobsDBPostsEntity).map(_ => Created(jobsDBPostsEntity.id))

  def findById(id: String)(implicit ec: ExecutionContext) : Future[Option[JobsDBPosts]] =
    collection.find(queryById(id)).one[JobsDBPosts]

  def deleteById(id: String)(implicit ec: ExecutionContext) : Future[Deleted.type] =
    collection.remove(queryById(id)).map(_ => Deleted)

  def find(implicit ec: ExecutionContext) : Future[List[BSONDocument]] =
    collection.find(emptyQuery).cursor[BSONDocument]().collect[List](20)

  private def queryById(id: String) = BSONDocument("_id" -> id)

  private def emptyQuery = BSONDocument()

}
