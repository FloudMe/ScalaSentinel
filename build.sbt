import scala.Console.in
ThisBuild / scalaVersion := "2.13.16"

lazy val commonSettings = Seq(
  organization := "com.scalasentinel",
  version := "0.1.0",
  libraryDependencies ++= Seq(
    "com.typesafe" % "config" % "1.4.3",
    "org.scalatest" %% "scalatest" % "3.2.19" % Test
  )
)

lazy val collector = (project in file("modules/collector"))
  .settings(
    commonSettings,
    name := "collector",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http" % "10.5.3",
      "com.typesafe.akka" %% "akka-stream" % "2.8.8",
      "com.typesafe.akka" %% "akka-stream-kafka" % "4.0.2",
      "io.spray" %%  "spray-json" % "1.3.6",
      "ch.qos.logback" % "logback-classic" % "1.5.17",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5",
      "com.typesafe.akka" %% "akka-stream-testkit" % "2.8.8" % Test,
      "com.typesafe.akka" %% "akka-http-testkit" % "10.5.3" % Test
    )
  )

//lazy val analyzer = (project in file("modules/analyzer"))
//  .settings(
//    commonSettings,
//    name := "analyzer",
//    libraryDependencies ++= Seq(
//      "org.apache.spark" %% "spark-core" % "3.4.0",
//      "org.apache.spark" %% "spark-sql" % "3.4.0",
//      "com.johnsnowlabs.nlp" %% "spark-nlp" % "5.0.0",
//      "org.apache.spark" %% "spark-sql-kafka-0-10" % "3.4.0"
//    )
//  )
//
//lazy val storage = (project in file("modules/storage"))
//  .settings(
//    commonSettings,
//    name := "storage",
//    libraryDependencies ++= Seq(
//      "com.datastax.oss" % "java-driver-core" % "4.15.0",
//      "org.postgresql" % "postgresql" % "42.5.4",
//      "org.apache.spark" %% "spark-cassandra-connector" % "3.4.0"
//    )
//  )
//
//lazy val alert = (project in file("modules/alert"))
//  .settings(
//    commonSettings,
//    name := "alert",
//    libraryDependencies ++= Seq(
//      "com.typesafe.akka" %% "akka-actor" % "2.8.0",
//      "com.slack.api" % "slack-api-client" % "1.29.0"
//    )
//  )
//
//lazy val gateway = (project in file("modules/gateway"))
//  .settings(
//    commonSettings,
//    name := "gateway",
//    libraryDependencies ++= Seq(
//      "com.typesafe.akka" %% "akka-http" % "10.5.0",
//      "com.typesafe.akka" %% "akka-stream" % "2.8.0"
//    )
//  )

lazy val root = (project in file("."))
  .aggregate(collector/*, analyzer, storage, alert, gateway*/)
  .settings(
    name := "ScalaSentinel",
    publish / skip := true
  )