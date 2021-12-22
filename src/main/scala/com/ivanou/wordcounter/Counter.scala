package com.ivanou.wordcounter

import scala.collection.immutable.ListMap

import cats.Show
import com.ivanou.wordcounter.SortOrder.{MaxCount, MinCount, Natural, SortOrder}

class Counter(val order: SortOrder) {

  val TopFrequentSize = 10

  private val topWords = collection.mutable.Set[String]()

  private var totalCount = 0
  private val wordFrequencies = collection.mutable.Map[String, Int]().withDefaultValue(0)

  def count(word: String) {
    val count = wordFrequencies(word) + 1
    wordFrequencies.update(word, count)
    updateTopWords(word, count)
    totalCount += 1
  }

  private def updateTopWords(word: String, count: Int) {
    if (topWords.contains(word)) {
      return
    }

    if (topWords.size < TopFrequentSize) {
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

  def isEmpty: Boolean = wordFrequencies.isEmpty

  def total: Int = totalCount

  def mostFrequentWords: Array[(String, Int)] =
    topWords.map { w => (w, wordFrequencies(w)) }.toArray.sortWith(_._2 > _._2)

}

object Counter {

  implicit val showCounter: Show[Counter] = Show.show { counter =>
    val sorted = counter.order match {
      case Natural =>
        counter.wordFrequencies.toSeq.sorted
      case MaxCount =>
        counter.wordFrequencies.toSeq.sortWith(_._2 > _._2)
      case MinCount =>
        counter.wordFrequencies.toSeq.sortWith(_._2 < _._2)
    }
    ListMap(sorted: _*).foldLeft("") { case (res, (k, v)) => s"$res$k, $v\n" }
  }

  implicit val showMostFrequent: Show[Array[(String, Int)]] = Show.show { mostFrequent =>
    mostFrequent.foldLeft("") { case (res, (word, count)) => s"$res$word $count\n" }
  }

  def apply(order: SortOrder): Counter = new Counter(order)
}
