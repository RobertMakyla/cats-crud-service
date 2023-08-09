package org.robmaksoftware.repo


import java.util.UUID

import cats.effect.Sync
import cats.syntax.flatMap._
import cats.syntax.functor._
import scala.collection.mutable.HashMap

import org.robmaksoftware.domain.Person
import org.robmaksoftware.domain.PersonId


final case class RepoInMem[F[_] : Sync](private val people: HashMap[PersonId, Person]) extends Repo[F, PersonId, Person] {

  private val F = Sync[F]

  val makeId: F[String] = F.delay {
    UUID.randomUUID().toString
  }

  override def get(id: PersonId): F[Option[Person]] = F.delay {
    people.get(id)
  }

  override def add(item: Person): F[PersonId] =
    for {
      id <- makeId.map(PersonId)
      _ <- F.delay {
        people.addOne(id -> item)
      }
    } yield id

  override def update(id: PersonId, newItem: Person): F[Int] =
    for {
      removedPersonOpt <- F.delay(people.remove(id))
      res <- if (removedPersonOpt.nonEmpty) F.delay {
        people.addOne(id -> newItem)
      }.as(1) else F.pure(0)
    } yield res


  override def delete(id: PersonId): F[Int] =
    for {
      removedPersonOpt <- F.delay(people.remove(id))
      res <- if (removedPersonOpt.nonEmpty) F.pure(1) else F.pure(0)
    } yield res

  override def all: fs2.Stream[F, Person] = fs2.Stream.emits(people.values.toSeq)

  override def allOrderByJoined: fs2.Stream[F, Person] = fs2.Stream.emits(people.values.toSeq.sortBy(_.joined))
}

