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
        offset.traverse(validateIsGreaterOrEqual(0, _)),
        limit.traverse(validateIsSmallerOrEqual(maxLimit, _))
      )


    val validatedOk: ValidatedNel[String, F[HttpResource.GetAllPeopleResponse]] = validatedParams.mapN((validOffset, validLimit) =>
      service
        .all(validOffset.getOrElse(0), validLimit.getOrElse(maxLimit))
        .compile
        .toList
        .map { ls: List[PersonWithId] =>
          HttpResource.GetAllPeopleResponse.Ok(PeopleDto(ls.map(_.toDto)))
        }
    )

    validatedOk.fold(errors => Monad[F].pure(HttpResource.GetAllPeopleResponse.BadRequest(errors.toList.mkString("; "))), identity)

  }

  override def getPerson(respond: HttpResource.GetPersonResponse.type)(personId: PersonId): F[HttpResource.GetPersonResponse] = {

    val validatedResponse: Validated[NonEmptyList[String], F[HttpResource.GetPersonResponse]] = validateIsNonEmpty(personId)
      .map { validPersonId =>

        service.get(validPersonId).map {
          case Some(person) => HttpResource.GetPersonResponse.Ok(person.toDtoWithId(validPersonId))
          case None => HttpResource.GetPersonResponse.NotFound
        }
      }

    validatedResponse.fold(errors => Monad[F].pure(HttpResource.GetPersonResponse.BadRequest(errors.toList.mkString("; "))), identity)
  }


}
