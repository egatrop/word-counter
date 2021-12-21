package com.ivanou.wordcounter

import java.io.{BufferedWriter, FileWriter}

import scala.io.Source
import scala.util.{Failure, Try, Using}

import cats.implicits._
import com.ivanou.wordcounter.utils.{StringOps, withTimer}
import com.typesafe.scalalogging.StrictLogging

object Runner extends App with StrictLogging {

  private val config = Config.parseArgs(args)

  private val counter = Counter(config.order)

  withTimer {
    for {
      _ <- countWords()
      if !counter.isEmpty
      _ <- writeResult()
    } yield ()
  }

  def countWords() = {
    Using(Source.fromFile(config.in)) { file =>
      logger.info(s"Starting to parse text from [${config.in}]")
      file
        .getLines()
        .flatMap(_.asWords)
        .foreach(counter.insert)
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
