package org.robmaksoftware.service

import java.time.temporal.ChronoUnit

import cats.Functor
import cats.syntax.functor._
import org.robmaksoftware.domain._
import org.robmaksoftware.repo.Repo

final class PersonService[F[_] : Functor](repo: Repo[F, PersonId, Person])(implicit compiler: fs2.Compiler[F, F]) {

  def add(p: Person): F[PersonId] = repo.add(p)

  def count: F[Long] = repo.all.compile.count

  def sumCredits: F[Long] =
    repo
      .all
      .scan(0L)(_ + _.credit)
      .compile
      .last
      .map(_.getOrElse(0L))

  def creditsPerDate: fs2.Stream[F, DateCredits] =
    repo
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
