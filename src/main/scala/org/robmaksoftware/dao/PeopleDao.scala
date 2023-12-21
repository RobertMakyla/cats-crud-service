package org.robmaksoftware.dao

import java.time.Instant
import java.util.UUID

import cats.effect.MonadCancelThrow
import doobie.Fragment
import doobie.Fragments
import doobie._
import doobie.ConnectionIO
import doobie.Read
import doobie.Transactor
import doobie.implicits._
import doobie.implicits.toSqlInterpolator
import org.robmaksoftware.domain._
import org.robmaksoftware.metas._

trait PeopleDao[F[_]] extends Dao[F, PersonId, Person, PersonWithId] {

  // Read

  def get(id: PersonId): F[Option[Person]]

  def all: fs2.Stream[F, PersonWithId]

  def allOrderByJoined: fs2.Stream[F, PersonWithId]

  // Write

  def add(item: Person): F[PersonId]

  def update(id: PersonId, newItem: Person): F[Int]

  def delete(id: PersonId): F[Int]

}

object PeopleDao {

  implicit class PeopleDaoOps(dao: PeopleDao[doobie.ConnectionIO]) {

    def to[F[_]: MonadCancelThrow](implicit xa: Transactor[F]): PeopleDao[F] = new PeopleDao[F] {

      override def get(id: PersonId): F[Option[Person]] = dao.get(id).transact(xa)

      override def all: fs2.Stream[F, PersonWithId] = dao.all.transact(xa)

      override def allOrderByJoined: fs2.Stream[F, PersonWithId] = dao.allOrderByJoined.transact(xa)

      override def add(item: Person): F[PersonId] = dao.add(item).transact(xa)

      override def update(id: PersonId, newItem: Person): F[Int] = dao.update(id, newItem).transact(xa)

      override def delete(id: PersonId): F[Int] = dao.delete(id).transact(xa)
    }
  }

  private val tableName         = "PEOPLE"
  private val tableNameFr       = Fragment.const(tableName)
  private val pk                = "id"
  private val valueCols         = List("name", "age", "sex", "credit", "joined")
  private val valueColsToUpdate = valueCols.map(col => s"$col = ?").mkString(", ")
  private val valueColsFr       = Fragment.const(valueCols.mkString(", "))
  private val allColsFr         = Fragment.const((pk :: valueCols).mkString(", "))

  private def whereId(id: PersonId): Fragment = Fragments.whereAnd(fr"id = $id")

  def makeDao: PeopleDao[doobie.ConnectionIO] = new PeopleDao[ConnectionIO] {

    implicit val logHandler = MyLogHandler.mySlf4jLogHandler

    private def personValsToInsert(p: Person) = fr"${p.name}, ${p.age}, ${p.sex}, ${p.credit}, ${p.joined}"

    private def newId: PersonId = PersonId(UUID.randomUUID().toString)

    override def get(id: PersonId): ConnectionIO[Option[Person]] =
      sql"SELECT $valueColsFr FROM $tableNameFr ${whereId(id)} LIMIT 1"
        .query[Person]
        .option

    override def all: fs2.Stream[ConnectionIO, PersonWithId] =
      sql"SELECT ${Fragment.const(pk)}, $valueColsFr FROM $tableNameFr"
        .query[PersonWithId]
        .stream

    override def allOrderByJoined: fs2.Stream[ConnectionIO, PersonWithId] =
      sql"SELECT ${Fragment.const(pk)}, $valueColsFr FROM $tableNameFr ORDER BY joined"
        .query[PersonWithId]
        .stream

    override def add(p: Person): ConnectionIO[PersonId] = {
      val id: PersonId = newId
      sql"INSERT INTO $tableNameFr ($allColsFr) VALUES ($id, ${personValsToInsert(p)}) ".update.run
        .map(_ => id)
    }

    override def update(id: PersonId, p: Person): ConnectionIO[Int] =
      Update[(String, Int, Sex, Double, Instant, PersonId)](
        s"UPDATE $tableName SET $valueColsToUpdate where $pk = ?"
      ).run((p.name, p.age, p.sex, p.credit, p.joined, id))

    override def delete(id: PersonId): ConnectionIO[Int] = sql"DELETE FROM $tableNameFr ${whereId(id)}".update.run
  }

}
