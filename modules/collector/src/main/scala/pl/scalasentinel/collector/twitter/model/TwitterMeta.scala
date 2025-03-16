package pl.scalasentinel.collector.twitter.model

case class TwitterMeta(
  newest_id: Option[String],
  oldest_id: Option[String],
  result_count: Int,
  next_token: Option[String]
)
