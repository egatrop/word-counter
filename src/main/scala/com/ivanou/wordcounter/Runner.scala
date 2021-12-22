package com.ivanou.wordcounter

import java.io.{BufferedWriter, FileWriter}

import scala.concurrent.duration.DurationInt
import scala.io.Source
import scala.util.{Try, Using}

import akka.actor.ActorSystem
import cats.implicits._
import com.ivanou.wordcounter.Counter.showArray
import com.ivanou.wordcounter.Counter.showMap
import com.ivanou.wordcounter.metrics.withJmxMetrics
import com.ivanou.wordcounter.utils.withTimer
import com.typesafe.scalalogging.StrictLogging

object Runner extends App with StrictLogging {

  private val config = Config.parseArgs(args)

  private val counter = withJmxMetrics(Counter(config.order, config.topN))

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
      _ <- fromFile(counter.countWords)
      if !counter.isEmpty
      _ = logTotalAndTopN()
      _ <- writeCsv()
    } yield ()
  }

  logScheduler.cancel()
  as.terminate()

  private def fromFile(f: Iterator[String] => Unit): Try[Unit] = {
    Using(Source.fromFile(config.in)) { file =>
      logger.info(s"Starting to parse text from [${config.in}]")
      f(file.getLines())
    }.recover(e => {
      logger.error(s"Failed to count the words: ${e.getMessage}")
      None
    })
  }

  private def logTotalAndTopN() {
    logger.info(s"Total number of words [${counter.total}]")
    logger.info(s"""Top ${counter.topN} most frequent words:
                   |${counter.mostFrequentWords.show}
                   |""".stripMargin)
  }

  private def writeCsv() = {
    Using(new FileWriter(config.out, false)) { fw =>
      Using(new BufferedWriter(fw)) { bw =>
        bw.write(counter.sortedFrequencies(config.order).show)
      }.recover(e => {
        logger.error(s"Failed to write data to [${config.out}]", e)
        e
      }).get
    }
  }
}
