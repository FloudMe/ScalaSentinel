package pl.scalasentinel.collector.twitter.model

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import pl.scalasentinel.collector.twitter.http.TwitterHttpApi

class TwitterHttpApiSpec extends AnyWordSpec with Matchers {
  "TwitterHttpApi" should {
    "generate valid URI for hashtag" in {
      val uri = TwitterHttpApi.createTwitterUri("scala", 50)
      uri.toString() shouldBe "https://api.twitter.com/2/tweets/search/recent?query=scala&max_results=50"
    }
  }
}
