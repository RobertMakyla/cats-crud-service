package org.robmaksoftware.howthisworks

import cats.syntax.show._
import org.robmaksoftware.arbitrary.ContractGenerator
import org.robmaksoftware.domain.{Contract, Job}
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks._

class SimplePropsSpec extends AnyFreeSpec with Matchers {

  implicit val smallBigIntArbitrary: Arbitrary[Contract[Job]] = ContractGenerator.arbitraryContract

  "contract" in {

    forAll { c: Contract[Job] ⇒
      println(c.show)
    }
  }
}
