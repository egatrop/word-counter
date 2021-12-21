ThisBuild / organization := "com.ivanou"
ThisBuild / scalaVersion := "2.13.6"

lazy val akkaVersion = "2.6.18"
lazy val logBackVersion = "1.2.3"
lazy val scoptVersion = "3.7.1"
lazy val scalaTestVersion = "3.2.10"
lazy val scalaLoggingVersion = "3.9.2"

libraryDependencies ++= Seq(
  "com.typesafe.akka"          %% "akka-stream"         % akkaVersion,
  "org.typelevel"              %% "cats-core"           % "2.1.1",
  "ch.qos.logback"              % "logback-classic"     % logBackVersion,
  "com.github.scopt"           %% "scopt"               % scoptVersion,
  "com.typesafe.scala-logging" %% "scala-logging"       % scalaLoggingVersion,
  "com.typesafe.akka"          %% "akka-stream-testkit" % akkaVersion      % Test,
  "org.scalatest"              %% "scalatest"           % scalaTestVersion % Test
)

lazy val root = (project in file("."))
  .settings(
    name := "word-counter",
    version := "0.1.0-SNAPSHOT",
    assemblyJarName := "counter.jar"
  )
