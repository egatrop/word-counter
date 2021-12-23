package com.ivanou.wordcounter

import scala.io.Source
import scala.util.Using

import cats.implicits.toShow
import com.ivanou.wordcounter.Counter.showArray
import com.ivanou.wordcounter.SortOrder.MaxCount
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatest.prop.TableDrivenPropertyChecks

class CounterSpec extends AnyFlatSpec with TableDrivenPropertyChecks {

  "counter" should "count words and return top 10 most frequent" in {
    val table = Table(
      ("file", "total number", "top 10 words"),
      ("text1.txt", 326, Set("sed", "ipsum", "quis", "eu", "ip-sum", "vel", "et", "ultricies", "nunc", "elit")),
      ("text2.txt", 12, Set("this", "is", "and", "test", "simple", "very", "a")),
      ("text3.txt", 16, Set("ten", "eleven", "eight", "seven", "six", "five", "four", "three", "two", "one")),
      ("text4.txt", 0, Set.empty),
//      ("text5.txt", 1106474, Set("the", "of", "and", "to", "in", "a", "he", "that", "was", "his")),
      ("text6.txt", 12, Set("69", "123", "567", "67", "45", "345")),
      ("text7.txt", 10, Set("winter", "dict-say", "array", "dict-ion-array", "diction", "win-ter", "say", "dict"))
    )

    forAll(table) { case (file, total, words) =>
      val counter = Counter(order = MaxCount, topN = 10)
      countWordsFromFile(file, counter)
      println(counter.mostFrequentWords.show)
      counter.total shouldBe total
      counter.mostFrequentWords.map(_._1).toSet shouldBe words
    }
  }

  private def countWordsFromFile(file: String, counter: Counter): Unit = {
    Using(Source.fromResource(file)) { file =>
      counter.countWords(file.getLines())
    }
  }
}
