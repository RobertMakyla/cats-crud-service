package org.robmaksoftware.repo

import java.time.Instant
import java.util.UUID

import cats.effect.MonadCancelThrow
import doobie.Fragment
import doobie.Fragments
import doobie._
import doobie.ConnectionIO
import doobie.Write
import doobie.Read
import doobie.Transactor
import doobie.implicits._
import doobie.implicits.toSqlInterpolator
import org.robmaksoftware.domain._
import org.robmaksoftware.domain.Metas._

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

  def update(id: PersonId, newItem: Person): F[Int]

  def delete(id: PersonId): F[Int]

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

      override def update(id: PersonId, newItem: Person): F[Int] = dao.update(id, newItem).transact(xa)

      override def delete(id: PersonId): F[Int] = dao.delete(id).transact(xa)
    }
  }

  private val tableName = Fragment.const("PEOPLE")
  private val pk = "id"
  private val valueCols = List("name", "age", "sex", "credit", "joined")
  private val valueColsToUpdate = valueCols.map(col => s"$col = ?").mkString(", ")
  private val valueColsFr = Fragment.const(valueCols.mkString(", "))
  private val allColsFr = Fragment.const((pk :: valueCols).mkString(", "))

  private def whereId(id: PersonId): Fragment = Fragments.whereAnd(fr"$pk = $id")


  def makeReadDao(implicit readPersonEv: Read[Person]): PeopleDaoRead[ConnectionIO] = new PeopleDaoRead[ConnectionIO] {

    override def get(id: PersonId): ConnectionIO[Option[Person]] =
      sql"SELECT $valueColsFr FROM $tableName ${whereId(id)} LIMIT 1".query[Person].option

    override def all: fs2.Stream[ConnectionIO, Person] =
      sql"SELECT $valueColsFr FROM $tableName".query[Person].stream

    override def allOrderByJoined: fs2.Stream[ConnectionIO, Person] =
      sql"SELECT $valueColsFr FROM $tableName ORDER BY joined".query[Person].stream
  }


  def makeWriteDao: PeopleDaoWrite[ConnectionIO] = new PeopleDaoWrite[ConnectionIO] {


    private def personValsToInsert(p: Person) = fr"${p.name}, ${p.age}, ${p.sex}, ${p.credit}, ${p.joined}"

    private def newId: PersonId = PersonId(UUID.randomUUID().toString)

    override def add(p: Person): ConnectionIO[PersonId] = {
      val id: PersonId = newId
      sql"INSERT INTO $tableName ($allColsFr) VALUES ($id, ${personValsToInsert(p)}) ".update.run.map(_ => id)
    }

    override def update(id: PersonId, p: Person): ConnectionIO[Int] = Update[(String, Int, Sex, Double, Instant, PersonId)](
      s"UPDATE $tableName SET $valueColsToUpdate where $pk = ?"
    ).run((p.name, p.age, p.sex, p.credit, p.joined, id))

    override def delete(id: PersonId): ConnectionIO[Int] = sql"DELETE FROM $tableName ${whereId(id)}".update.run
  }

}