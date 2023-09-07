package org.robmaksoftware.http

import cats.Monad
import cats.syntax.apply._    // for mapN
import cats.syntax.functor._  // for F.map()
import cats.syntax.traverse._
import org.robmaksoftware.domain.{Person, PersonId, PersonWithId}
import org.robmaksoftware.http.definitions.{PeopleDto, PersonDto}
import org.robmaksoftware.service.PersonService
import org.robmaksoftware.http.{Resource => HttpResource}
import Converters._
import cats.data.{NonEmptyList, Validated, ValidatedNel}

class HandlerImpl[F[_] : Monad](
  service: PersonService[F]
)(
  implicit compiler: fs2.Compiler[F, F] // required for compiling fs2.Stream
)
  extends Handler[F] with Validators {

  val maxLimit = 500

  override def getAllPeople(respond: HttpResource.GetAllPeopleResponse.type)(offset: Option[Int], limit: Option[Int]): F[HttpResource.GetAllPeopleResponse] = {

    val validatedParams: (ValidatedNel[String, Option[Int]], ValidatedNel[String, Option[Int]]) =
      (
        offset.traverse(validateIsGreaterOrEqual("offset", 0, _)),
        limit.traverse(validateIsSmallerOrEqual("limit", maxLimit, _))
      )

    val validatedResponse: ValidatedNel[String, F[HttpResource.GetAllPeopleResponse]] = validatedParams.mapN((validOffset, validLimit) =>
      service
        .all(validOffset.getOrElse(0), validLimit.getOrElse(maxLimit))
        .compile
        .toList
        .map { ls: List[PersonWithId] =>
          HttpResource.GetAllPeopleResponse.Ok(PeopleDto(ls.map(_.toDto)))
        }
    )

    validatedResponse.fold(errors => Monad[F].pure(HttpResource.GetAllPeopleResponse.BadRequest(errors.toList.mkString("; "))), identity)
  }

  override def getPerson(respond: HttpResource.GetPersonResponse.type)(personId: PersonId): F[HttpResource.GetPersonResponse] = {

    val validatedResponse: Validated[NonEmptyList[String], F[HttpResource.GetPersonResponse]] = validateIsNonEmpty("person ID" , personId.value)
      .map { _ =>

        service.get(personId).map {
          case Some(person) => HttpResource.GetPersonResponse.Ok(person.toDtoWithId(personId))
          case None => HttpResource.GetPersonResponse.NotFound
        }
      }

    validatedResponse.fold(errors => Monad[F].pure(HttpResource.GetPersonResponse.BadRequest(errors.toList.mkString("; "))), identity)
  }

  override def deletePerson(respond: HttpResource.DeletePersonResponse.type)(personId: PersonId): F[HttpResource.DeletePersonResponse] = {

    val validatedResponse: Validated[NonEmptyList[String], F[HttpResource.DeletePersonResponse]] = validateIsNonEmpty("person ID" , personId.value)
      .map { _ =>
        service.delete(personId).map { deletedRows: Int =>
          if (deletedRows > 0) HttpResource.DeletePersonResponse.Ok else HttpResource.DeletePersonResponse.NotFound
        }
      }

    validatedResponse.fold(errors => Monad[F].pure(HttpResource.DeletePersonResponse.BadRequest(errors.toList.mkString("; "))), identity)

  }

  override def updatePerson(respond: Resource.UpdatePersonResponse.type)(personId: PersonId, body: PersonDto): F[Resource.UpdatePersonResponse] = {

    val validatedParams  =
      (
        validateIsNonEmpty("person ID" , personId.value),
        validateIsNonEmpty("person name" , body.name),
        validateRange("age", 0, 150, body.age ),
        validateSex(body.sex),
        validateInstant("joined", body.joined )
      )

    val validatedResponse: ValidatedNel[String, F[HttpResource.UpdatePersonResponse]] = validatedParams.mapN((vId, vName, vAge, vSex, vJoined ) =>
      service
        .update(PersonId(vId), Person(name = vName, age = vAge, sex = vSex, credit = body.credit, joined = vJoined))
        .map { updatedRows: Int =>
          if (updatedRows > 0) HttpResource.UpdatePersonResponse.Ok else HttpResource.UpdatePersonResponse.NotFound
        }
    )

    validatedResponse.fold(errors => Monad[F].pure(HttpResource.UpdatePersonResponse.BadRequest(errors.toList.mkString("; "))), identity)
  }

  override def createPerson(respond: HttpResource.CreatePersonResponse.type)(body: PersonDto): F[HttpResource.CreatePersonResponse] ={

    val validatedPerson: ValidatedNel[String, Person]   =
      (
        validateIsNonEmpty("person name" , body.name),
        validateRange("age", 0, 150, body.age ),
        validateSex(body.sex),
        validateInstant("joined", body.joined )
      )
        .mapN{ Person(_, _, _ , body.credit, _) }


    val validatedResponse: ValidatedNel[String, F[HttpResource.CreatePersonResponse]] = validatedPerson.map { person =>
      service
        .add(person)
        .map(HttpResource.CreatePersonResponse.Ok)
    }

    validatedResponse.fold(errors => Monad[F].pure(HttpResource.CreatePersonResponse.BadRequest(errors.toList.mkString("; "))), identity)

  }


}
