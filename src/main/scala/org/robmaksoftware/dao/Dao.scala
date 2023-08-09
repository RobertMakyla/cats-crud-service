package org.robmaksoftware.dao

import cats.effect.Sync
import cats.effect.Resource
import cats.effect.syntax.resource._
import org.robmaksoftware.domain.{Person, PersonId}

import scala.collection.mutable.HashMap

trait Dao[F[_], K, V] {

  def get(id: K): F[Option[V]]

  def add(item: V): F[K]

  def update(id: K, newItem: V): F[Int]

  def delete(id: K): F[Int]

  def all: fs2.Stream[F, V]

  def allOrderByJoined: fs2.Stream[F, V]
}

object Dao {
  def inMemDao[F[_] : Sync]: Dao[F, PersonId, Person] = new InMemPeopleDao[F](HashMap.empty)

}