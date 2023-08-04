package org.robmaksoftware.domain

import cats.Eq


case class PersonId(value: String) extends AnyVal

case class Person(name: String, age: Int, sex: Sex)

object Person {
  implicit val eq: Eq[Person] = Eq.fromUniversalEquals
}