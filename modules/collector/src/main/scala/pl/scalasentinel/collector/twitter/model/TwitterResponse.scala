package pl.scalasentinel.collector.twitter.model

case class TwitterResponse(
  data: Seq[Tweet],
  meta: Option[TwitterMeta] = None,
  errors: Option[Seq[String]] = None
)
