package org.robmaksoftware.howthisworks

import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks._

class SimplePropsSpec extends AnyFreeSpec with Matchers{

  type TwoInts = (Int, Int)

  private val smallBigIntGen: Gen[(Int, Int)] = for {
    n <- Gen.choose(1, 9)
    m <- Gen.choose(10, 100)
  } yield (n, m)

  implicit val smallBigIntArbitrary: Arbitrary[TwoInts] = Arbitrary(smallBigIntGen)


  "first int is smaller" in {

    forAll { smallBig: (Int, Int) =>
      val small = smallBig._1
      val big = smallBig._2

      small should be < big
    }
  }

  "first int is not equal to the second one" in {

    forAll { smallBig: (Int, Int) =>
      val small = smallBig._1
      val big = smallBig._2

      small should not equal big
    }
  }

}
