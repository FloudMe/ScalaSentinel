package pl.scalasentinel.collector.twitter.protocols

import pl.scalasentinel.collector.twitter.model.Tweet

object KafkaProducerProtocol {
  sealed trait Command

  case class SendTweets(tweets: Seq[Tweet]) extends Command

  case object Flush extends Command
}
