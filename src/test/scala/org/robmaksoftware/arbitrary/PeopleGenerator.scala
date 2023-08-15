package org.robmaksoftware.arbitrary

import java.time.Instant
import cats.syntax.flatMap._
import org.robmaksoftware.domain.{Person, Sex}
import org.scalacheck.{Arbitrary, Gen}
import org.scalacheck.Arbitrary._

object PeopleGenerator {

  def peopleGen: Gen[Person] = {
    for {
      name <- Gen.asciiStr
      age <- Gen. ??? todo
      sex <- Arbitrary(Gen.oneOf(Sex.values))
      credit <- arbDouble
      joined <- arbitrary[Instant]
    } yield Person(
      name = name,
      age = age,
      sex = sex,
      credit = credit,
      joined = joined
    )
  }
}
