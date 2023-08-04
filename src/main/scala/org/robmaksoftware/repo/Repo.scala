package org.robmaksoftware.repo

import java.util.UUID

import cats.effect.Sync
import cats.syntax.flatMap._
import cats.syntax.functor._
import scala.collection.mutable.HashMap

//import cats.implicits._
import org.robmaksoftware.domain.Person
import org.robmaksoftware.domain.PersonId

trait Repo[F[_], K, V] {

  def get(id: K): F[Option[V]]

  def add(item: V): F[K]

  def update(id: K, newItem: V): F[Unit]

  def delete(id: K): F[Unit]
}

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

  override def update(id: PersonId, newItem: Person): F[Unit] =
    for {
      _ <- F.delay {
        people.remove(id)
      }
      _ <- F.delay {
        people.addOne(id -> newItem)
      }
    } yield ()


  override def delete(id: PersonId): F[Unit] = F.delay {
    people.remove(id)
  }

}

object Repo {
  def inMem[F[_]: Sync] = new RepoInMem[F](HashMap.empty) //todo wrap it in Resource ?

}