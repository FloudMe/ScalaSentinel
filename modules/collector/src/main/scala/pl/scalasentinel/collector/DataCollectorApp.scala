package pl.scalasentinel.collector

import akka.actor.ActorSystem
import pl.scalasentinel.collector.twitter.TwitterCollector

object DataCollectorApp extends App {
  implicit val system: ActorSystem = ActorSystem("TwitterCollector")
  new TwitterCollector("scala")
}
