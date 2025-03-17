package pl.scalasentinel.collector

import akka.actor.{ActorSystem, Props}
import pl.scalasentinel.collector.twitter.actors.MasterActor
import pl.scalasentinel.collector.twitter.config.ConfigLoader
import pl.scalasentinel.collector.twitter.protocols.CollectorProtocol.StartCollecting

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

object DataCollectorApp extends App {
  implicit val system: ActorSystem = ActorSystem("TwitterCollectorSystem")
  private  val configLoader        = new ConfigLoader()
  private  val tagConfigs          = configLoader.loadTagConfigs()

  private val master = system.actorOf(Props[MasterActor], "master")

  tagConfigs.foreach { config =>
    master ! StartCollecting(config)
  }

  sys.addShutdownHook {
    system.terminate()
    Await.ready(system.whenTerminated, 30.seconds)
  }
}
