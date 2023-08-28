package org.robmaksoftware.service

import java.time.temporal.ChronoUnit

import cats.Functor
import cats.syntax.functor._
import org.robmaksoftware.domain._
import org.robmaksoftware.dao.Dao

final class PersonService[F[_] : Functor](
  dao: Dao[F, PersonId, Person, PersonWithId]
)(
  implicit compiler: fs2.Compiler[F, F] // required for fs2.Stream
) {

  def add(p: Person): F[PersonId] = dao.add(p)

  def get(id: PersonId): F[Option[Person]] = dao.get(id)

  def count: F[Long] = dao.all.compile.count

  def sumCredits: F[Double] =
    dao
      .all
      .scan(0d)(_ + _.person.credit)
      .compile
      .last
      .map(_.getOrElse(0d))

  def creditsPerDate: fs2.Stream[F, DateCredits] =
    dao
      .allOrderByJoined
      .groupAdjacentBy(_.person.joined.truncatedTo(ChronoUnit.DAYS))
      .map { case (epochday, chunk) =>

        val totalCredit = chunk
          .toList
          .map(_.person.credit)
          .sum

        DateCredits(epochday, chunk.size, totalCredit)
      }

  def all: fs2.Stream[F, PersonWithId] = dao.all

}
