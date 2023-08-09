package org.robmaksoftware.service

import java.time.temporal.ChronoUnit

import cats.Functor
import cats.syntax.functor._
import org.robmaksoftware.domain._
import org.robmaksoftware.dao.Dao

final class PersonService[F[_] : Functor](dao: Dao[F, PersonId, Person])(implicit compiler: fs2.Compiler[F, F]) {

  def add(p: Person): F[PersonId] = dao.add(p)

  def count: F[Long] = dao.all.compile.count

  def sumCredits: F[Double] =
    dao
      .all
      .scan(0d)(_ + _.credit)
      .compile
      .last
      .map(_.getOrElse(0d))

  def creditsPerDate: fs2.Stream[F, DateCredits] =
    dao
      .allOrderByJoined
      .groupAdjacentBy(_.joined.truncatedTo(ChronoUnit.DAYS))
      .map { case (epochday, chunk) =>

        val totalCredit = chunk
          .toList
          .map(_.credit)
          .sum

        DateCredits(epochday, chunk.size, totalCredit)
      }


}
