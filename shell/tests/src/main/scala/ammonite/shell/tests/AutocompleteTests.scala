package ammonite.shell
package tests

import utest._

class AutocompleteTests(check0: => Checker, checkSignatures: Boolean = true) extends TestSuite{

  val tests = TestSuite{
    val check = check0
    def complete(caretCode: String,
                 cmp: (Set[String]) => Set[String],
                 sigs: (Set[String]) => Set[String] = _ => Set()) = {
      val cursor = caretCode.indexOf("<caret>")
      val buf = caretCode.replace("<caret>", "")

      val (index, completions, signatures) = check.complete(cursor, buf)

      val left = cmp(completions.toSet)
      assert(left == Set())
      if (checkSignatures) {
        val sigLeft = sigs(signatures.toSet)
        assert(sigLeft == Set())
      }
    }

    // Not sure why clone and finalize don't appear in this list
    val anyCompletion = Set(
      "synchronized",
      "##", "!=", "==",
      "ne", "eq",
      "wait", "notifyAll", "notify",
      "toString", "equals", "hashCode",
      "getClass", "asInstanceOf", "isInstanceOf",
      "+", "formatted", "ensuring",
      "→", "->"
    ) ++ {
      if (scala.util.Properties.versionNumberString startsWith "2.10.")
        Set("__leftOfArrow ", "__resultOfEnsuring ", "x", "self ")
      else
        Set()
    }
    implicit class SetExt[T](s1: Set[T]) {
      def ^(s2: Set[T]): Set[T] = (s1 diff s2) | (s2 diff s1)
    }

    'import{
      complete("""import <caret>""", Set("java", "javax", "scala") -- _)
      complete("""import j<caret>""", Set("java", "javax") -- _)
      complete("""import ja<caret>""", x => Set("java", "javax") ^ (x - "javafx"))
      complete("""import java.<caret>""", Set("lang", "util") -- _)
      complete("""import java.u<caret>""", Set("util") ^ _)
      complete("""import java.util.<caret>""", Set("LinkedHashMap", "LinkedHashSet") -- _)
      complete("""import java.util.LinkedHa<caret>""", Set("LinkedHashMap", "LinkedHashSet") ^ _)
      complete("""import java.util.{LinkedHa<caret>""", Set("LinkedHashMap", "LinkedHashSet") ^ _)
      complete(
        """import java.util.{LinkedHashMap, Linke<caret>""",
        Set("LinkedHashMap", "LinkedHashSet", "LinkedList") ^ _
      )

    }

    'scope{
      complete("""<caret>""", Set("scala") -- _)
      complete("""Seq(1, 2, 3).map(argNameLol => <caret>)""", Set("argNameLol") -- _)
      complete("""object Zomg{ <caret> }""", Set("Zomg") -- _)
      complete(
        "printl<caret>",
        Set("println") ^,
        Set[String]() ^
      )
//      Not sure why this doesnt work
//      complete(
//        "println<caret>",
//        Set[String]() ^,
//        Set("def println(x: Any): Unit", "def println(): Unit") ^
//      )
    }
    'scopePrefix{
      complete("""ammon<caret>""", Set("ammonite", "ammonium") ^ _)

      complete("""Seq(1, 2, 3).map(argNameLol => argNam<caret>)""", Set("argNameLol") ^)

      complete("""object Zomg{ Zom<caret> }""", Set("Zomg") ^)
      complete("""object Zomg{ Zo<caret>m }""", Set("Zomg") ^)
      complete("""object Zomg{ Z<caret>om }""", Set("Zomg") ^)
      complete("""object Zomg{ <caret>Zom }""", Set("Zomg") ^)
    }
    'dot{
      complete("""java.math.<caret>""",
        Set("MathContext", "BigDecimal", "BigInteger", "RoundingMode") ^
      )

      complete( """scala.Option.<caret>""",
        (anyCompletion ++ Set("apply", "empty", "option2Iterable")) ^
      )

      complete( """Seq(1, 2, 3).map(_.<caret>)""",
        (anyCompletion ++ Set("+", "-", "*", "/")) -- _
      )

      complete( """val x = 1; x + (x.<caret>)""",
        Set("-", "+", "*", "/") -- _
      )
    }

    'deep{
      complete("""fromN<caret>""",
        Set("scala.concurrent.duration.fromNow") ^
      )
      complete("""Fut<caret>""",
        Set("scala.concurrent.Future", "java.util.concurrent.Future") -- _
      )
      complete("""SECO<caret>""",
        Set("scala.concurrent.duration.SECONDS") ^
      )
    }
    'dotPrefix{
      complete("""java.math.Big<caret>""",
        Set("BigDecimal", "BigInteger") ^
      )
      complete("""scala.Option.option2<caret>""",
        Set("option2Iterable") ^
      )
      complete("""val x = 1; x + x.><caret>""",
        Set(">>", ">>>") -- _,
        Set(
          "def >(x: Double): Boolean",
          "def >(x: Float): Boolean",
          "def >(x: Int): Boolean",
          "def >(x: Short): Boolean",
          "def >(x: Long): Boolean",
          "def >(x: Char): Boolean",
          "def >(x: Byte): Boolean"
        ) ^
      )
      // https://issues.scala-lang.org/browse/SI-9153
      //
      //      complete("""val x = 123; x + x.m<caret>""",
      //        Set("max"),
      //        _ -- _
      //      )

      val compares = Set("compare", "compareTo")
      complete("""Seq(1, 2, 3).map(_.compa<caret>)""", compares ^)
      complete("""Seq(1, 2, 3).map(_.co<caret>mpa)""", compares ^)
//      complete("""Seq(1, 2, 3).map(_.<caret>compa)""", compares, ^)
    }
  }
}
