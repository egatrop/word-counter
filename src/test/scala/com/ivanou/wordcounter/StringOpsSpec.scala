package com.ivanou.wordcounter

import com.ivanou.wordcounter.utils.StringOps
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatest.prop.TableDrivenPropertyChecks

class StringOpsSpec extends AnyFlatSpec with TableDrivenPropertyChecks {

  "a line" should "be split properly into words in lower case" in {
    val table = Table(
      ("line", "words"),
      ("hello-world", Seq("hello-world")),
      ("hello--world", Seq("hello--world")),
      ("Hello World!", Seq("hello", "world")),
      (" * & ! = ' %#  $@ ", Seq.empty),
      (
        """Adhaesiones "adhuc" DesiDErabile: dicit doloribus,
          |epicureum equos-errata *erroribus* fidelissimae
          |fugiamus homo texit?!&!""".stripMargin,
        Seq(
          "adhaesiones",
          "adhuc",
          "desiderabile",
          "dicit",
          "doloribus",
          "epicureum",
          "equos-errata",
          "erroribus",
          "fidelissimae",
          "fugiamus",
          "homo",
          "texit"
        )
      )
    )

    forAll(table) { case (line, words) =>
      line.asWords shouldBe words
    }
  }
}
