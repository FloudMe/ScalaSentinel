package pl.scalasentinel.collector.twitter.actors

import akka.Done
import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.scaladsl.Source
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import pl.scalasentinel.collector.twitter.model.Tweet
import pl.scalasentinel.collector.twitter.protocols.KafkaProducerProtocol.{Flush, SendTweets}
import pl.scalasentinel.collector.twitter.protocols.{KafkaProducerProtocol, TwitterJsonProtocol}
import spray.json.enrichAny

import scala.concurrent.Future
import scala.util.{Failure, Success}

class KafkaProducerActor(bootstrapServers: String, topic: String)
  extends Actor with ActorLogging with TwitterJsonProtocol {
  implicit val system: ActorSystem = context.system


  private val producerSettings = ProducerSettings(
    context.system,
    new StringSerializer,
    new StringSerializer
  ).withBootstrapServers(bootstrapServers)

  override def receive: Receive = {
    case SendTweets(tweets) => handleKafkaSend(tweets)

    case Flush => ???
  }

  private def handleKafkaSend(tweets: Seq[Tweet]): Unit = {
    sendToKafka(tweets).onComplete {
      case Success(_)  => log.debug(s"Successfully sent ${tweets.size} tweets to Kafka")
      case Failure(ex) => log.error(s"Failed to send tweets to Kafka: ${ex.toString}")
    }(context.dispatcher)
  }

  private def sendToKafka(tweets: Seq[Tweet]): Future[Done] = {
    val records = tweets.map { tweet =>
      new ProducerRecord[String, String](topic, tweet.toJson.compactPrint)
    }
    Source(records).runWith(Producer.plainSink(producerSettings))
  }
}

object KafkaProducerActor {
  def props(bootstrapServers: String, topic: String): Props =
    Props(new KafkaProducerActor(bootstrapServers, topic))
}
