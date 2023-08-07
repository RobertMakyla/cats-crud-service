package org.robmaksoftware.repo

import cats.effect.Sync
import cats.effect.Resource
import cats.effect.syntax.resource._

import scala.collection.mutable.HashMap

trait Repo[F[_], K, V] {

  def get(id: K): F[Option[V]]

  def add(item: V): F[K]

  def update(id: K, newItem: V): F[Unit]

  def delete(id: K): F[Unit]

  def all: fs2.Stream[F, V]

  def allOrderByJoined: fs2.Stream[F, V]
}

object Repo {
  def inMem[F[_] : Sync] = new RepoInMem[F](HashMap.empty) //todo wrap it in Resource ?

}