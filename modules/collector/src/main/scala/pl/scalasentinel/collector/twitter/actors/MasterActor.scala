package pl.scalasentinel.collector.twitter.actors

import akka.actor.SupervisorStrategy.{Escalate, Restart}
import akka.actor.{Actor, ActorLogging, OneForOneStrategy, Props, SupervisorStrategy}
import pl.scalasentinel.collector.twitter.exceptions.TwitterApiException
import pl.scalasentinel.collector.twitter.protocols.CollectorProtocol.{CollectionFailure, CollectionResult, FetchTweets, StartCollecting}

class MasterActor extends Actor with ActorLogging {
  private lazy val kafkaProducer = context.actorOf(
    KafkaProducerActor.props("localhost:9093", "raw-tweets"),
    "kafka-producer"
  )

  def receive: Receive = {
    case StartCollecting(config) =>
      val fetcher = context.actorOf(
        Props(new FetcherActor(config, kafkaProducer)),
        s"fetcher-${config.name.replace("#", "")}"
      )
      fetcher ! FetchTweets

    case CollectionResult(tag, count) => log.info(s"Successfully collected $count tweets for #$tag")

    case CollectionFailure(tag, reason) => log.error(s"Failed to collect tweets for #$tag: $reason")
  }

  override val supervisorStrategy: SupervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 3) {
      case _: TwitterApiException => Restart
      case _                      => Escalate
    }
}