package org.robmaksoftware.service

import java.time.temporal.ChronoUnit
import cats.effect.Sync
import cats.syntax.functor._
import cats.syntax.flatMap._
import org.robmaksoftware.domain._
import org.robmaksoftware.dao.Dao
import org.typelevel.log4cats.slf4j.Slf4jLogger

abstract class PersonService[F[_]] {

  def add(p: Person): F[PersonId]

  def get(id: PersonId): F[Option[Person]]

  def delete(id: PersonId): F[Int]

  def update(id: PersonId, newValue: Person): F[Int]

  def count: F[Long]

  def sumCredits: F[Double]

  def creditsPerDate: fs2.Stream[F, DateCredits]

  def all(offset: Int, limit: Int): fs2.Stream[F, PersonWithId]
}

object PersonService {

  def apply[F[_]: Sync](dao: Dao[F, PersonId, Person, PersonWithId])(implicit
      compiler: fs2.Compiler[F, F] // required for compiling fs2.Stream
  ) = new PersonService[F] {

    private val logger = Slf4jLogger.getLogger[F]

    def add(p: Person): F[PersonId] = dao.add(p)

    def get(id: PersonId): F[Option[Person]] =
      for {
        _   <- logger.info("GET " + id.value)
        res <- dao.get(id)
      } yield res

    def count: F[Long] = dao.all.compile.count

    def sumCredits: F[Double] =
      dao.all
        .scan(0d)(_ + _.person.credit)
        .compile
        .last
        .map(_.getOrElse(0d))

    def creditsPerDate: fs2.Stream[F, DateCredits] =
      dao.allOrderByJoined
        .groupAdjacentBy(_.person.joined.truncatedTo(ChronoUnit.DAYS))
        .map { case (epochday, chunk) =>
          val totalCredit = chunk.toList
            .map(_.person.credit)
            .sum

          DateCredits(epochday, chunk.size, totalCredit)
        }

    def all(offset: Int, limit: Int): fs2.Stream[F, PersonWithId] =
      dao.allOrderByJoined
        .drop(offset)
        .take(limit)

    def delete(id: PersonId): F[Int] = dao.delete(id)

    def update(id: PersonId, newValue: Person): F[Int] = dao.update(id, newValue)
  }

}
