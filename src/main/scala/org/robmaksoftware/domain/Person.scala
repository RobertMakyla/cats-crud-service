package org.robmaksoftware.domain

import java.time.Instant

import cats.Eq
import doobie.util.Write


case class PersonId(value: String) extends AnyVal

case class Person(name: String, age: Int, sex: Sex, credit: Double, joined: Instant)

object Person {
  implicit val eq: Eq[Person] = Eq.fromUniversalEquals

}