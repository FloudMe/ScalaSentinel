package pl.scalasentinel.collector.twitter.http

import akka.http.scaladsl.model.Uri
import com.typesafe.config.ConfigFactory

object TwitterHttpApi {
  private val config = ConfigFactory.load()
  private val apiUrl: String = config.getString("twitter.api-url")
  private val defaultMaxResults = config.getInt("twitter.default-max-results")

  def createTwitterUri(hashtag: String, maxResults: Int = defaultMaxResults): Uri = {
    val queryParams = Map("query" -> s"#$hashtag", "max_results" -> maxResults.toString)
    Uri(apiUrl).withQuery(Uri.Query(queryParams))
  }

}
