package com.alpha.jobsDBPosting

import akka.actor.ActorSystem
import akka.http.javadsl.server.Route
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.{HttpCharsets, MediaType}
import akka.http.scaladsl.model.MediaType.WithFixedCharset
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.stream.{ActorMaterializer, Materializer}
import com.alpha.jobsDBPosting.db.JobsDBPostsManager
import com.alpha.jobsDBPosting.models._
import spray.json._
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._

import scala.concurrent.ExecutionContext
import scala.io.StdIn.readLine
import akka.http.scaladsl.settings.ParserSettings
import akka.http.scaladsl.settings.ServerSettings

trait RestApi {

  import JobsDBPostsProtocol._
  import JobsDBPostsListsProtocol._
  import JobsDBPostsEntity._
  import JobsDBPostsEntityProtocol.EntityFormat


  implicit val system: ActorSystem

  implicit val materializer: Materializer

  implicit val ec: ExecutionContext

  val route =
    cors() {
      pathPrefix("jobs-db-postings") {
        //      (post & entity(as[JobsDBPosts])) { jobsDBPosts =>
        //        complete {
        //          JobsDBPostsManager
        //            .save(jobsDBPosts) map { r =>
        //            Created -> Map("id" -> r.id).toJson
        //          }
        //        }
        //      } ~
        (get & path(Segment)) { id =>
          complete {
            JobsDBPostsManager
              .findById(id)
              .map(t => OK -> t)
          }
        } ~
          (delete & path(Segment)) { id =>
            complete {
              JobsDBPostsManager.deleteById(id) map { _ =>
                NoContent
              }
            }
          } ~
          (get) {
            complete {
              JobsDBPostsManager.find map { ts =>
                OK -> {
                  val kam = ts
                    .map(_.as[JobsDBPosts].data)
                  JobsDBPostsLists(kam)
                }
              }
            }
          }
      }
    }


}

object Api extends App with RestApi {

  override implicit val system: ActorSystem = ActorSystem("rest-api")

  override implicit val materializer: Materializer = ActorMaterializer()

  override implicit val ec: ExecutionContext = system.dispatcher

  val utf8 = HttpCharsets.`UTF-8`
  val `application/vnd.api+json`: WithFixedCharset =
    MediaType.customWithFixedCharset("application", "vnd.api+json", utf8)

  // add custom media type to parser settings:
  val parserSettings = ParserSettings(system).withCustomMediaTypes(`application/vnd.api+json`)
  val serverSettings = ServerSettings(system).withParserSettings(parserSettings)

  val bindingFuture = Http().bindAndHandle(route, "localhost", 9488, settings = serverSettings)

  println(s"Server online at http://localhost:9488/\nPress RETURN to stop...")
  readLine()

  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())

}