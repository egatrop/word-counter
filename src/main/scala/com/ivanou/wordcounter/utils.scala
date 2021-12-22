package com.ivanou.wordcounter

import com.typesafe.scalalogging.StrictLogging

object utils extends StrictLogging {

  def withTimer(f: => Unit): Unit = {
    val start = System.currentTimeMillis
    f
    val stop = System.currentTimeMillis
    logger.info(s"Completed in ${stop - start}ms")
  }

  implicit class StringOps(str: String) {

    def asWords: Array[String] =
      str
        .toLowerCase
        .replaceAll("[^A-Za-z-]", " ")
        .split(" ")
        .filter(_.nonEmpty)
  }

}
