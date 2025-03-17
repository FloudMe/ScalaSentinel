package pl.scalasentinel.collector.twitter.config

import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.duration._
import scala.jdk.CollectionConverters._

class ConfigLoader {
  private val config       : Config        = ConfigFactory.load()
  private val defaultConfig: DefaultConfig = DefaultConfig(config.getConfig("collector.default"))
  private val tagsConfig   : Seq[Config]   = config.getConfigList("collector.tags").asScala.toSeq

  def loadTagConfigs(): Seq[TagConfig] = {
    tagsConfig.map(prepareTagConfig).filter(_.enabled).sortBy(_.priority)
  }

  private def prepareTagConfig(tagConfig: Config) = {
    TagConfig(
      name = tagConfig.getString("name"),
      interval = if (tagConfig.hasPath("interval")) tagConfig.getDuration("interval").toMillis.millis else defaultConfig.interval,
      maxResults = if (tagConfig.hasPath("max-results")) tagConfig.getInt("max-results") else defaultConfig.maxResult,
      enabled = if (tagConfig.hasPath("enabled")) tagConfig.getBoolean("enabled") else defaultConfig.enabled,
      priority = if (tagConfig.hasPath("priority")) tagConfig.getInt("priority") else defaultConfig.priority
    )
  }
}
