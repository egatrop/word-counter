package com.ivanou.wordcounter

import java.util.concurrent.atomic.AtomicInteger

import scala.collection.immutable.ListMap

import cats.Show
import com.ivanou.wordcounter.SortOrder.{MaxCount, MinCount, Natural, SortOrder}

class Counter(val order: SortOrder) {

  private val map = collection.concurrent.TrieMap[String, Int]().withDefaultValue(0)

  private val wc = new AtomicInteger(0)

  def insert(word: String) {
    wc.incrementAndGet()
    map.update(word, map(word) + 1)
  }

  def isEmpty: Boolean = map.isEmpty

  def total: Int = wc.get()
}

object Counter {

  implicit val showCounter: Show[Counter] = Show.show { counter =>
    val sorted = counter.order match {
      case Natural =>
        counter.map.toSeq.sorted
      case MaxCount =>
        counter.map.toSeq.sortWith(_._2 > _._2)
      case MinCount =>
        counter.map.toSeq.sortWith(_._2 < _._2)
    }
    ListMap(sorted: _*).foldLeft("") { case (res, (k, v)) => s"$res$k $v\n" }
  }

  def apply(order: SortOrder) = new Counter(order)
}
