package pl.scalasentinel.collector.twitter.protocols

import pl.scalasentinel.collector.twitter.model.{Tweet, TwitterMeta, TwitterResponse}
import spray.json.{DefaultJsonProtocol, JsonFormat}

trait TwitterJsonProtocol extends DefaultJsonProtocol {
  implicit val tweetFormat   : JsonFormat[Tweet]           = jsonFormat2(Tweet)
  implicit val metaFormat    : JsonFormat[TwitterMeta]     = jsonFormat4(TwitterMeta)
  implicit val responseFormat: JsonFormat[TwitterResponse] = jsonFormat3(TwitterResponse)
}
