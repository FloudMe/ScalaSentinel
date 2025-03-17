package pl.scalasentinel.collector.twitter

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.GenericHttpCredentials
import akka.http.scaladsl.unmarshalling.Unmarshal
import com.typesafe.scalalogging.LazyLogging
import pl.scalasentinel.collector.twitter.config.TagConfig
import pl.scalasentinel.collector.twitter.exceptions.TwitterApiException
import pl.scalasentinel.collector.twitter.http.TwitterHttpApi
import pl.scalasentinel.collector.twitter.model.TwitterResponse
import pl.scalasentinel.collector.twitter.protocols.TwitterJsonProtocol
import spray.json._

import scala.concurrent.Future
import scala.concurrent.duration._

class TwitterCollector(tagConfig: TagConfig)(implicit system: ActorSystem) extends TwitterJsonProtocol with LazyLogging {

  import system.dispatcher

  private val httpTimeout = 30.seconds
  private val hashtag     = tagConfig.name
  private val maxResults  = tagConfig.maxResults
  private val uri         = TwitterHttpApi.createTwitterUri(hashtag, maxResults)

  private lazy val bearerToken = sys.env.getOrElse("TWITTER_BEARER_TOKEN", throw new Exception("Token not found!"))

  private lazy val request = HttpRequest(
    method = HttpMethods.GET,
    uri = uri,
    headers = List(headers.Authorization(GenericHttpCredentials("Bearer", bearerToken)))
  )

  def fetch(): Future[TwitterResponse] = {
    Http()
      .singleRequest(request)
      .flatMap(handleResponse)
      .recover {
        case ex: Exception =>
          throw new TwitterApiException(s"Failed to fetch tweets for #${tagConfig.name}", ex)
      }
  }

  private def handleResponse(response: HttpResponse): Future[TwitterResponse] = {
    if (response.status.isSuccess()) {
      Unmarshal(response.entity).to[String].map(parseResponse)
    } else {
      Future.failed(new TwitterApiException(s"HTTP error: ${response.status}"))
    }
  }

  private def parseResponse(json: String) = {
    val jsonAst  = json.parseJson
    val response = jsonAst.convertTo[TwitterResponse]

    response.errors match {
      case Some(errors) => throw new TwitterApiException(s"Twitter API errors: ${errors.mkString(", ")}")
      case None         => response
    }
  }
}

object TwitterCollector {
  def apply(tagConfig: TagConfig)(implicit system: ActorSystem): TwitterCollector = {
    new TwitterCollector(tagConfig)
  }
}