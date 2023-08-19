package org.robmaksoftware.domain

import cats.Eq

case class PersonId(value: String) extends AnyVal

object PersonId {
  implicit val eq: Eq[PersonId] = Eq.fromUniversalEquals
}