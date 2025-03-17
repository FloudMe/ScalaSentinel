package pl.scalasentinel.collector.twitter.actors

import akka.actor.SupervisorStrategy.{Escalate, Restart}
import akka.actor.{Actor, ActorLogging, OneForOneStrategy, Props, SupervisorStrategy}
import pl.scalasentinel.collector.twitter.exceptions.TwitterApiException
import pl.scalasentinel.collector.twitter.protocols.CollectorProtocol.{CollectionFailure, CollectionResult, FetchTweets, StartCollecting}

class MasterActor extends Actor with ActorLogging {

  def receive: Receive = {
    case StartCollecting(config) =>
      val fetcher = context.actorOf(
        Props(new FetcherActor(config)),
        s"fetcher-${config.name.replace("#", "")}"
      )
      fetcher ! FetchTweets

    case CollectionResult(tag, count) =>
      log.info("Successfully collected {} tweets for #{}", count, tag)

    case CollectionFailure(tag, reason) =>
      log.error("Failed to collect tweets for #{}: {}", tag, reason)
  }

  override val supervisorStrategy: SupervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 3) {
      case _: TwitterApiException => Restart
      case _                      => Escalate
    }
}