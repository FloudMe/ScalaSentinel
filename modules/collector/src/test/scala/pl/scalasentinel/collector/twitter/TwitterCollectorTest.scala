package pl.scalasentinel.collector.twitter

import akka.http.scaladsl.model._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.unmarshalling.Unmarshal
import org.scalatest.concurrent.ScalaFutures.convertScalaFuture
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import pl.scalasentinel.collector.twitter.model.{Tweet, TwitterMeta, TwitterResponse}
import pl.scalasentinel.collector.twitter.protocols.TwitterJsonProtocol
import spray.json._

class TwitterCollectorTest extends AnyWordSpec
  with Matchers
  with ScalatestRouteTest
  with TwitterJsonProtocol {

  "TwitterCollector" should {
    "correctly parse full Twitter response" in {
      val jsonResponse =
        """{
          |  "data": [
          |    {"id": "1", "text": "Scala 3 is awesome!"},
          |    {"id": "2", "text": "Akka streams FTW"}
          |  ],
          |  "meta": {
          |    "newest_id": "2",
          |    "oldest_id": "1",
          |    "result_count": 2,
          |    "next_token": "next_page_token"
          |  }
          |}""".stripMargin

      val result = Unmarshal(HttpEntity(ContentTypes.`application/json`, jsonResponse))
        .to[String].map { json =>
          val jsonAst = json.parseJson
          jsonAst.convertTo[TwitterResponse]
        }
        .futureValue

      result shouldEqual TwitterResponse(
        data = Seq(
          Tweet("1", "Scala 3 is awesome!"),
          Tweet("2", "Akka streams FTW")
        ),
        meta = Some(TwitterMeta(
          newest_id = Some("2"),
          oldest_id = Some("1"),
          result_count = 2,
          next_token = Some("next_page_token")
        )
        )
      )
    }
  }
}
