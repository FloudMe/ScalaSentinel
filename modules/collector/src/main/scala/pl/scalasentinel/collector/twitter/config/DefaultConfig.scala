package pl.scalasentinel.collector.twitter.config

import com.typesafe.config.Config

import scala.concurrent.duration.{DurationLong, FiniteDuration}

case class DefaultConfig(config: Config) {
  val interval : FiniteDuration = config.getDuration("interval").toMillis.millis
  val maxResult: Int            = config.getInt("max-results")
  val enabled  : Boolean        = config.getBoolean("enabled")
  val priority : Int            = config.getInt("priority")
}
