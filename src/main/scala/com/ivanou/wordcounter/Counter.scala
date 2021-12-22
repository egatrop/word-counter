package com.ivanou.wordcounter

import scala.collection.immutable.ListMap

import cats.Show
import com.ivanou.wordcounter.SortOrder.{MaxCount, MinCount, Natural, SortOrder, Unordered}
import com.ivanou.wordcounter.utils.StringOps

class Counter(val topN: Int) {

  private val topWords = collection.mutable.Set[String]()
  private var totalCount = 0
  private val wordFrequencies = collection.mutable.Map[String, Int]().withDefaultValue(0)

  def isEmpty: Boolean = wordFrequencies.isEmpty

  def total: Int = totalCount

  def mostFrequentWords: Array[(String, Int)] =
    topWords.map { w => (w, wordFrequencies(w)) }.toArray.sortWith(_._2 > _._2)

  def countWords(lines: Iterator[String]) {
    lines.flatMap(_.asWords).foreach(count)
  }

  def sortedFrequencies(order: SortOrder): Map[String, Int] = {
    val sorted = order match {
      case Natural =>
        wordFrequencies.toSeq.sorted
      case MaxCount =>
        wordFrequencies.toSeq.sortWith(_._2 > _._2)
      case MinCount =>
        wordFrequencies.toSeq.sortWith(_._2 < _._2)
      case Unordered =>
        wordFrequencies.toSeq
    }
    ListMap(sorted: _*)
  }

  private def count(word: String) {
    val count = wordFrequencies(word) + 1
    wordFrequencies.update(word, count)
    updateTopWords(word, count)
    totalCount += 1
  }

  private def updateTopWords(word: String, count: Int) {
    if (topWords.contains(word)) {
      return
    }

    if (topWords.size < topN) {
      topWords.add(word)
      return
    }

    var leastTopFrequentWord = topWords.head
    var leastTopFrequentCount = wordFrequencies(leastTopFrequentWord)

    topWords.foreach { word =>
      val count = wordFrequencies(word)
      if (count < leastTopFrequentCount) {
        leastTopFrequentWord = word
        leastTopFrequentCount = count
      }
    }

    if (leastTopFrequentCount < count) {
      topWords.remove(leastTopFrequentWord)
      topWords.add(word)
    }
  }
}

object Counter {

  implicit val showMap: Show[Map[String, Int]] = Show.show { map =>
    map.foldLeft("") { case (res, (k, v)) => s"$res$k, $v\n" }
  }

  implicit val showArray: Show[Array[(String, Int)]] = Show.show { arr =>
    arr.foldLeft("") { case (res, (word, count)) => s"$res$word $count\n" }
  }

  def apply(order: SortOrder, topN: Int): Counter = new Counter(topN)
}
