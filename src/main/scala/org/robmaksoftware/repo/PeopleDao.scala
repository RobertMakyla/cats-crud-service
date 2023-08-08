package org.robmaksoftware.repo

import cats.effect.MonadCancelThrow
import doobie.Fragment
import doobie.Fragments
import doobie._
import doobie.implicits._
import doobie.implicits.toSqlInterpolator
import org.robmaksoftware.domain.Person
import org.robmaksoftware.domain.PersonId

trait PeopleDao[F[_]]
  extends Repo[F, PersonId, Person]
    with PeopleDaoWrite[F]
    with PeopleDaoRead[F]


trait PeopleDaoRead[F[_]] {

  def get(id: PersonId): F[Option[Person]]

  def all: fs2.Stream[F, Person]

  def allOrderByJoined: fs2.Stream[F, Person]
}

trait PeopleDaoWrite[F[_]] {

  def add(item: Person): F[PersonId]

  def update(id: PersonId, newItem: Person): F[Unit]

  def delete(id: PersonId): F[Unit]

}

object PeopleDao {


  implicit class PeopleDaoReadOps(dao: PeopleDaoRead[ConnectionIO]) {

    def connIo_to_f[F[_] : MonadCancelThrow](implicit xa: Transactor[F]): PeopleDaoRead[F] = new PeopleDaoRead[F] {

      override def get(id: PersonId): F[Option[Person]] = dao.get(id).transact(xa)

      override def all: fs2.Stream[F, Person] = dao.all.transact(xa)

      override def allOrderByJoined: fs2.Stream[F, Person] = dao.allOrderByJoined.transact(xa)
    }
  }

  implicit class PeopleDaoWriteOps(dao: PeopleDaoWrite[ConnectionIO]) {

    def connIo_to_f[F[_] : MonadCancelThrow](implicit xa: Transactor[F]): PeopleDaoWrite[F] = new PeopleDaoWrite[F] {

      override def add(item: Person): F[PersonId] = dao.add(item).transact(xa)

      override def update(id: PersonId, newItem: Person): F[Unit] = dao.update(id, newItem).transact(xa)

      override def delete(id: PersonId): F[Unit] = dao.delete(id).transact(xa)
    }
  }

  private val tableName = Fragment.const("PEOPLE")
  private val valueCols = Fragment.const("name, age, sex, credit, joined")

  private def whereId(id: PersonId): Fragment = Fragments.whereAnd(fr"id=$id")

  def makeReadDao(implicit readPersonEv: Read[Person]): PeopleDaoRead[ConnectionIO] = new PeopleDaoRead[ConnectionIO] {

    override def get(id: PersonId): ConnectionIO[Option[Person]] =
      sql"SELECT $valueCols FROM $tableName ${whereId(id)} LIMIT 1".query[Person].option

    override def all: fs2.Stream[ConnectionIO, Person] =
      sql"SELECT $valueCols FROM $tableName ".query[Person].stream

    override def allOrderByJoined: fs2.Stream[ConnectionIO, Person] =
      sql"SELECT $valueCols FROM $tableName ORDER BY joined ".query[Person].stream
  }

  def makeWriteDao(): PeopleDaoWrite[ConnectionIO] = new PeopleDaoWrite[ConnectionIO] {

    override def add(item: Person): ConnectionIO[PersonId] = ???

    override def update(id: PersonId, newItem: Person): ConnectionIO[Unit] = ???

    override def delete(id: PersonId): ConnectionIO[Unit] = ???
  }

}