package org.robmaksoftware.domain

import java.time.Instant
import cats.Eq

case class Person(name: String, age: Int, sex: Sex, credit: Double, joined: Instant)

object Person {
  implicit val eq: Eq[Person] = Eq.fromUniversalEquals
}
