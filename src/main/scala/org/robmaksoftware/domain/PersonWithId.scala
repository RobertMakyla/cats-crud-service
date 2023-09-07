package org.robmaksoftware.domain

import cats.Eq

case class PersonWithId(id: PersonId, person: Person)

object PersonWithId {
  implicit val eq: Eq[PersonWithId] = Eq.fromUniversalEquals
}
