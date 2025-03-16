package pl.scalasentinel.collector.twitter

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.GenericHttpCredentials
import akka.http.scaladsl.unmarshalling.Unmarshal
import pl.scalasentinel.collector.twitter.model.TwitterResponse
import pl.scalasentinel.collector.twitter.protocols.TwitterJsonProtocol
import spray.json._
import com.typesafe.scalalogging.LazyLogging
import pl.scalasentinel.collector.twitter.http.TwitterHttpApi

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success, Try}

class TwitterCollector(hashtag: String)(implicit system: ActorSystem) extends TwitterJsonProtocol with LazyLogging {
  private val httpTimeout = 30.seconds

  import system.dispatcher

  private val bearerToken = sys.env.getOrElse("TWITTER_BEARER_TOKEN", throw new Exception("Token not found!"))

  private val request = HttpRequest(
    method = HttpMethods.GET,
    uri = TwitterHttpApi.createTwitterUri(hashtag),
    headers = List(headers.Authorization(GenericHttpCredentials("Bearer", bearerToken)))
  )

  private val responseFuture: Future[TwitterResponse] = Http()
    .singleRequest(request)
    .flatMap { response =>
      if (response.status.isSuccess()) {
        Unmarshal(response.entity).to[String].map { json =>
          val jsonAst = json.parseJson
          val response = jsonAst.convertTo[TwitterResponse]

          response.errors match {
            case Some(errors) =>
              throw new Exception(s"API Errors: ${errors.mkString(", ")}")
            case None => response
          }
        }
      } else {
        Future.failed(new Exception(s"Error: ${response.status}"))
      }
    }


  responseFuture.onComplete {
    case Success(response) =>
      logger.info(s"Successfully downloaded ${response.data.size} tweets for hashtag: #$hashtag")
      response.data.foreach(tweet => logger.debug(s"Tweet ID: ${tweet.id}, Text: ${tweet.text}"))
      system.terminate()
    case Failure(ex)       =>
      logger.error(s"Failed to fetch tweets for hashtag: #$hashtag. Reason: ${ex.getMessage}", ex)
      system.terminate()
  }

  Try {
    Await.result(system.whenTerminated, httpTimeout)
  } match {
    case Success(_)         =>
    case Failure(exception) =>
      logger.error("Error while fetching. Forcing termination.", exception)
      system.terminate()
  }
}