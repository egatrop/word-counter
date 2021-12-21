package com.ivanou.wordcounter

import java.io.{BufferedWriter, FileWriter}

import scala.concurrent.duration.DurationInt
import scala.io.Source
import scala.util.{Failure, Try, Using}

import akka.actor.ActorSystem
import cats.implicits._
import com.ivanou.wordcounter.metrics.withJmxMetrics
import com.ivanou.wordcounter.utils.{withTimer, StringOps}
import com.typesafe.scalalogging.StrictLogging

object Runner extends App with StrictLogging {

  private val config = Config.parseArgs(args)

  private val counter = withJmxMetrics(Counter(config.order))

  private val as = ActorSystem("word-counter")

  private val logScheduler = {
    val s = as.scheduler
    s.scheduleWithFixedDelay(1.second, 1.second) { () =>
      {
        logger.info(s"Number of words counted [${counter.total}]")
      }
    }(as.dispatcher)
  }

  withTimer {
    for {
      _ <- countWords()
      if !counter.isEmpty
      _ <- writeResult()
    } yield ()
  }

  logScheduler.cancel()
  as.terminate()

  def countWords() = {
    Using(Source.fromFile(config.in)) { file =>
      logger.info(s"Starting to parse text from [${config.in}]")
      file
        .getLines()
        .flatMap(_.asWords)
        .foreach(counter.count)
    }.recover(e => {
      logger.error(s"Failed to count the words: ${e.getMessage}")
      Failure(e)
    })
  }

  def writeResult(): Try[Unit] = {
    Using(new FileWriter(config.out, false)) { fw =>
      Using(new BufferedWriter(fw)) { writer =>
        writer.write(counter.show)
      }.fold(
        e => logger.error(s"Failed to write data to [${config.out}]", e),
        _ => logger.info(s"Total number of words [${counter.total}]")
      )
    }
  }
}
