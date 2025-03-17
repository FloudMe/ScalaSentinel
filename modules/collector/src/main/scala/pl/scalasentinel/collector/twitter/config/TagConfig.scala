package pl.scalasentinel.collector.twitter.config

import scala.concurrent.duration.FiniteDuration

case class TagConfig(
  name: String,
  interval: FiniteDuration,
  maxResults: Int,
  enabled: Boolean,
  priority: Int
)
