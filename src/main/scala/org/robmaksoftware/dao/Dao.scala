package org.robmaksoftware.dao

import cats.effect.kernel.Resource
import cats.effect.{Async, MonadCancelThrow, Sync}
import cats.effect.syntax.resource._
import doobie.util.transactor.Transactor
import org.robmaksoftware.dao.PeopleDao._
import org.robmaksoftware.db.DbTransactor
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

  type DaoResource[F[_]] =  Resource[F, Dao[F, PersonId, Person]]


  def inMemDao[F[_] : Sync]: DaoResource[F] =
    Sync[F].delay {
      new PeopleInMemDao[F](HashMap.empty)
    }.toResource


  def dbDao[F[_] : MonadCancelThrow : Async]: DaoResource[F]  =
    for {
      transactor <- DbTransactor.sqlite(flywayMigration = true)
    } yield {
      implicit val xa: Transactor[F] = transactor
      PeopleDao.makeDao.to[F]
    }

}