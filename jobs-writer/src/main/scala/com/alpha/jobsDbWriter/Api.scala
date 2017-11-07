package com.alpha.jobsDbWriter

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.stream.{ActorMaterializer, Materializer}
import com.alpha.jobsDbWriter.db.JobsManager
import com.alpha.jobsDbWriter.models._
import com.typesafe.config.{Config, ConfigFactory}
import spray.json._

import scala.concurrent.ExecutionContext
import scala.io.StdIn.readLine

trait RestApi {

  import JobsProtocol._
  import JobsListsProtocol._
  import JobsEntity._
  import JobsEntityProtocol.EntityFormat


  implicit val system: ActorSystem

  implicit val materializer: Materializer

  implicit val ec: ExecutionContext

  val route =
    pathPrefix("jobsWriter") {
      (post & entity(as[Jobs])) { jobs =>
        complete {
          JobsManager
            .save(jobs) map { r =>
            Created -> r
          }
        }
      } ~
        (get & path(Segment)) { id =>
          complete {
            JobsManager
              .findById(id)
              .map(t => OK -> t)
          }
        } ~
        (delete & path(Segment)) { id =>
          complete {
            JobsManager.deleteById(id) map { _ =>
              NoContent
            }
          }
        } ~
        (get) {
          complete {
            JobsManager.find map { ts =>
              OK -> {
                val jobs = ts
                  .map(_.as[Jobs].data)
                JobsLists(jobs)
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

  val config : Config = ConfigFactory.load()

  val port : Int = config.getInt("http.port")

  val bindingFuture = Http().bindAndHandle(route, "localhost", port)

  println(s"Server online at http://localhost:$port/\nPress RETURN to stop...")
  readLine()

  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())

}
