package com.ivanou.wordcounter

import com.typesafe.scalalogging.StrictLogging

object utils extends StrictLogging {

  implicit class StringOps(str: String) {

    def asWords: Array[String] =
      str
        .toLowerCase
        .replaceAll("[^A-Za-z0-9-]", " ")
        .split(" ")
        .filter(_.nonEmpty)
  }
}
