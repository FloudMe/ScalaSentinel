package pl.scalasentinel.collector.twitter.protocols

import pl.scalasentinel.collector.twitter.config.TagConfig

object CollectorProtocol {
  sealed trait Command

  case class StartCollecting(tagConfig: TagConfig) extends Command

  case class CollectionResult(tag: String, count: Int) extends Command

  case class CollectionFailure(tag: String, reason: String) extends Command

  case object FetchTweets extends Command
}
