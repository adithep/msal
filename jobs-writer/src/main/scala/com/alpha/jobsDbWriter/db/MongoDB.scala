package com.alpha.jobsDbWriter.db

import com.typesafe.config.{Config, ConfigFactory}
import reactivemongo.api.{MongoConnection, MongoDriver}

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global

object MongoDB {

  val config : Config = ConfigFactory.load()
  val database : String = config.getString("mongodb.database")
  val servers : mutable.Buffer[String] = config.getStringList("mongodb.servers").asScala

  val driver = new MongoDriver
  val connection : MongoConnection = driver.connection(servers)

  val db = connection(database)
}