package org.robmaksoftware.http

import cats.Monad
import cats.syntax.applicative._
import cats.syntax.contravariantSemigroupal._
import cats.syntax.functor._
import cats.syntax.monad._
import cats.syntax.traverse._
import org.robmaksoftware.domain.{PersonId, PersonWithId}
import org.robmaksoftware.http.definitions.PeopleDto
import org.robmaksoftware.service.PersonService
import org.robmaksoftware.http.{Resource => HttpResource}
import Converters._
import cats.data.{Validated, ValidatedNec}
import org.robmaksoftware.http.Resource.GetAllPeopleResponse

class HandlerImpl[F[_] : Monad](
  service: PersonService[F]
)(
  implicit compiler: fs2.Compiler[F, F] // required for compiling fs2.Stream
) extends Handler[F] {

  val maxLimit = 500

  override def getAllPeople(respond: HttpResource.GetAllPeopleResponse.type)(offset: Option[Int], limit: Option[Int]): F[HttpResource.GetAllPeopleResponse] =

    (
      offset.traverse(validateIsGreaterOrEqual(0, _))
      ,
      limit.traverse(validateIsSmallerOrEqual(maxLimit, _))
    ).mapN{ (validOffset, validLimit) =>
      val xxx: F[GetAllPeopleResponse.Ok] = service
        .all(validOffset.getOrElse(0), validLimit.getOrElse(maxLimit))
        .compile
        .toList
        .map { ls: List[PersonWithId] =>
          HttpResource.GetAllPeopleResponse.Ok(PeopleDto(ls.map(_.toDto)))
        }
      xxx
    }.fold{ _ => GetAllPeopleResponse.BadRequest , identity }


  override def getPerson(respond: HttpResource.GetPersonResponse.type)(personId: PersonId): F[HttpResource.GetPersonResponse] =
    ???
  //    validatePersonId(personId).fla
  //    service.get(personId).map {
  //      case Some(p) => HttpResource.GetPersonResponse.Ok(p.toDtoWithId(personId))
  //      case None => HttpResource.GetPersonResponse.NotFound
  //    }


  def validateIsGreaterOrEqual(min: Int, value: Int) = Validated.condNec(value >= min, value, s"$value is less than min value $min")

  def validateIsSmallerOrEqual(max: Int, value: Int) = Validated.condNec(value <= max, value, s"$value is greater than max value $max")

  def validatePersonId(id: => PersonId)  = Validated.condNec(id.value.nonEmpty, id, "ID is empty")


}
