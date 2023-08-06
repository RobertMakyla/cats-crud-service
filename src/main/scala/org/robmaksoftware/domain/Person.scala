package org.robmaksoftware.domain

import java.time.Instant

import cats.Eq


case class PersonId(value: String) extends AnyVal

case class Person(name: String, age: Int, sex: Sex, credit: Long, joined: Instant)

object Person {
  implicit val eq: Eq[Person] = Eq.fromUniversalEquals
}