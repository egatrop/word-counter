package com.ivanou.wordcounter

import com.ivanou.wordcounter.SortOrder.{Natural, SortOrder}
import com.typesafe.scalalogging.StrictLogging

case class Config(in: String = "text.txt", out: String = "out.csv", order: SortOrder = Natural)

object SortOrder extends Enumeration {
  type SortOrder = Value

  val MaxCount: SortOrder.Value = Value("max")
  val MinCount: SortOrder.Value = Value("min")
  val Natural: SortOrder.Value = Value("natural")

  def fromString(s: String): SortOrder = {
    SortOrder
      .values
      .find(_.toString == s)
      .getOrElse(Natural)
  }
}

object Config extends StrictLogging {

  private val parser = new scopt.OptionParser[Config]("Word Counter") {
    head("Word Counter Console")
    opt[String]('i', "input").optional() valueName "<input>" action { (i, c) =>
      c.copy(in = i)
    } text "File to count words from"
    opt[String]('o', "output").optional() valueName "<output>" action { (o, c) =>
      c.copy(out = o)
    } text "File to write result to"
    opt[String]('s', "sort").optional() valueName "<sort>" action { (s, c) =>
      c.copy(order = SortOrder.fromString(s))
    } text
      """Sorting order(natural by default):
        |`max` for the most frequently used words first
        |`min` for the least frequently used words first
        |`natural` for natural sorting by world""".stripMargin
    help("help") text "prints usage text"

    override def showUsageOnError = true

  }

  def parseArgs(args: Array[String]): Config = {
    parser.parse(args, Config()) match {
      case Some(conf) => conf
      case None =>
        logger.warn("Warning: Could not parse arguments, will use default values")
        Config()
    }
  }
}
