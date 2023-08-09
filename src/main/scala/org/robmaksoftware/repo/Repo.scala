package org.robmaksoftware.repo

import cats.effect.Sync
import cats.effect.Resource
import cats.effect.syntax.resource._
import org.robmaksoftware.domain.{Person, PersonId}

import scala.collection.mutable.HashMap

trait Repo[F[_], K, V] {

  def get(id: K): F[Option[V]]

  def add(item: V): F[K]

  def update(id: K, newItem: V): F[Int]

  def delete(id: K): F[Int]

  def all: fs2.Stream[F, V]

  def allOrderByJoined: fs2.Stream[F, V]
}

object Repo {
  def inMem[F[_] : Sync]: Repo[F, PersonId, Person] = new RepoInMem[F](HashMap.empty)

}