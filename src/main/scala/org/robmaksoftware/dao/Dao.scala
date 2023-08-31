package org.robmaksoftware.dao

import cats.effect.kernel.Resource
import cats.effect.{Async, MonadCancelThrow, Sync}
import cats.effect.syntax.resource._
import doobie.util.transactor.Transactor
import org.robmaksoftware.dao.PeopleDao._
import org.robmaksoftware.db.DbTransactor
import org.robmaksoftware.domain.{PersonWithId, Person, PersonId}

import scala.collection.mutable.HashMap

trait Dao[F[_], Key, Value, KeyAndValue] {

  def get(id: Key): F[Option[Value]]

  def add(item: Value): F[Key]

  def update(id: Key, newItem: Value): F[Int]

  def delete(id: Key): F[Int]

  def all: fs2.Stream[F, KeyAndValue]

  def allOrderByJoined: fs2.Stream[F, KeyAndValue]
}

object Dao {

  type DaoResource[F[_]] = Resource[F, Dao[F, PersonId, Person, PersonWithId]]


  def inMemDao[F[_] : Sync]: DaoResource[F] =
    Sync[F].delay {
      new PeopleInMemDao[F](HashMap.empty)
    }.toResource


  def sqliteDao[F[_] : MonadCancelThrow : Async]: DaoResource[F] =
    for {
      implicit0(xa: Transactor[F]) <- DbTransactor.sqlite(flywayMigration = true) // implicit0 comes from plugin: https://github.com/oleg-py/better-monadic-for
    } yield PeopleDao.makeDao.to[F]


}