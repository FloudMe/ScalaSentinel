package pl.scalasentinel.collector.twitter.actors

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Cancellable, Scheduler}
import pl.scalasentinel.collector.twitter.TwitterCollector
import pl.scalasentinel.collector.twitter.config.TagConfig
import pl.scalasentinel.collector.twitter.protocols.CollectorProtocol._
import pl.scalasentinel.collector.twitter.protocols.KafkaProducerProtocol.SendTweets

import scala.util.{Failure, Success}

class FetcherActor(config: TagConfig, kafkaProducer: ActorRef) extends Actor with ActorLogging {

  import context.dispatcher

  implicit val system: ActorSystem = context.system

  protected val collector  : TwitterCollector    = TwitterCollector(config)
  protected val scheduler  : Scheduler           = context.system.scheduler
  protected var cancellable: Option[Cancellable] = None

  override def preStart(): Unit = {
    super.preStart()

    cancellable = Some(
      scheduler.scheduleAtFixedRate(
        initialDelay = config.interval,
        interval = config.interval
      )(() => self ! FetchTweets)
    )
  }

  override def postStop(): Unit = {
    cancellable.foreach(_.cancel())
    super.postStop()
  }

  def receive: Receive = {
    case FetchTweets =>
      collector.fetch().onComplete {
        case Success(response) =>
          kafkaProducer ! SendTweets(response.data)
          context.parent ! CollectionResult(config.name, response.data.size)
        case Failure(ex)       => context.parent ! CollectionFailure(config.name, ex.toString)
      }
  }
}
