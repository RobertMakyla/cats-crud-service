package org.robmaksoftware.arbitrary

import java.time.Instant

import org.robmaksoftware.domain.{Person, Sex}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}

object PeopleGenerator {

  implicit val arbitraryPerson: Arbitrary[Person] = Arbitrary{
    for {
      name <- Gen.asciiStr
      age <- Gen.choose(0,120)
      sex <- Gen.oneOf(Sex.values)
      credit <- Gen.choose(0.0D,100D)
      joined <- Gen.choose(0, Instant.now().toEpochMilli).map(Instant.ofEpochMilli)
    } yield Person(
      name = name,
      age = age,
      sex = sex,
      credit = credit,
      joined = joined
    )
  }

  implicit val arbitraryTwoPeople: Arbitrary[(Person, Person)] = Arbitrary {
    for {
      genP1 <- arbitrary[Person]
      genP2 <- arbitrary[Person]
    } yield (genP1, genP2)
  }
}
