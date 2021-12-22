package com.ivanou.wordcounter

import com.ivanou.wordcounter.Config.{In, Out, TopN}
import com.ivanou.wordcounter.SortOrder.{SortOrder, Unordered}
import com.typesafe.scalalogging.StrictLogging

case class Config(
    in: String = In,
    out: String = Out,
    order: SortOrder = Unordered,
    topN: Int = TopN
)

object SortOrder extends Enumeration {
  type SortOrder = Value

  val MaxCount: SortOrder.Value = Value("max")
  val MinCount: SortOrder.Value = Value("min")
  val Natural: SortOrder.Value = Value("natural")
  val Unordered: SortOrder.Value = Value("none")

  def fromString(s: String): SortOrder = {
    SortOrder
      .values
      .find(_.toString == s)
      .getOrElse(Unordered)
  }
}

object Config extends StrictLogging {

  val In = "text.txt"
  val Out = "out.csv"
  val TopN = 10

  private val parser = new scopt.OptionParser[Config]("Word Counter") {
    head("Word Counter Console")

    opt[String]('i', "input").optional() valueName "<input>" action { (i, c) =>
      c.copy(in = i)
    } text s"File to count words from. '$In' by default "

    opt[String]('o', "output").optional() valueName "<output>" action { (o, c) =>
      c.copy(out = o)
    } text s"File to write result to. '$Out' by default"

    opt[Int]('n', "topN").optional() valueName "<top N>" action { (n, c) =>
      c.copy(topN = n)
    } text s"Number of most frequent words to print. $TopN by default"

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
      case Unordered =>
        logger.warn("Warning: Could not parse arguments, will use default values")
        Config()
    }
  }
}
