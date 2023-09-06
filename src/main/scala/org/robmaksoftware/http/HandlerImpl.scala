package org.robmaksoftware.http

import cats.Monad
import cats.syntax.apply._ // for mapN
import cats.syntax.functor._
import cats.syntax.traverse._
import org.robmaksoftware.domain.{PersonId, PersonWithId}
import org.robmaksoftware.http.definitions.PeopleDto
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

  def deletePerson(respond: HttpResource.DeletePersonResponse.type)(personId: org.robmaksoftware.domain.PersonId): F[HttpResource.DeletePersonResponse] = {

    val validatedResponse: Validated[NonEmptyList[String], F[HttpResource.DeletePersonResponse]] = validateIsNonEmpty("person ID" , personId.value)
      .map { _ =>
        service.delete(personId).map { deletedRows: Int =>
          if (deletedRows > 0) HttpResource.DeletePersonResponse.Ok else HttpResource.DeletePersonResponse.NotFound
        }
      }

    validatedResponse.fold(errors => Monad[F].pure(HttpResource.DeletePersonResponse.BadRequest(errors.toList.mkString("; "))), identity)

  }

}
