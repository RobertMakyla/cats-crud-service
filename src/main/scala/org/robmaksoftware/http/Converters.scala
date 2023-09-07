package org.robmaksoftware.http

import org.robmaksoftware.domain.{Person, PersonId, PersonWithId}
import org.robmaksoftware.http.definitions.PersonWithIdDto

object Converters {


  implicit class PersonOps(p: Person) {

    def toDtoWithId(id: PersonId): PersonWithIdDto =
      PersonWithIdDto(
        id = id.value,
        name = p.name,
        age = p.age,
        sex = p.sex.entryName,
        credit = p.credit,
        joined = p.joined.toEpochMilli
      )
  }

  implicit class PersoWithIdOps(p: PersonWithId) {

    def toDto: PersonWithIdDto = p.person.toDtoWithId(p.id)
  }


}
