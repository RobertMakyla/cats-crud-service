package org.robmaksoftware.dao

import cats.{Eq, MonadThrow}
import cats.syntax.eq._
import cats.syntax.flatMap._
import cats.syntax.functor._
import cats.syntax.traverse._
import org.robmaksoftware.domain.{PersonWithId, Person, PersonId}
import org.scalacheck.Prop._
import org.scalacheck.effect.PropF
import org.scalacheck.effect.PropF.forAllF
import org.scalacheck.util.Pretty.pretty
import org.scalacheck.{Arbitrary, Test}
import org.scalatest.matchers.should.Matchers

class PeopleDaoGenProps[F[_]: MonadThrow](
    dao: Dao[F, PersonId, Person, PersonWithId]
)(implicit
    personArb: Arbitrary[(Person, Person)],
    personEq: Eq[Person],
    personIdEq: Eq[PersonId],
    compileEv: fs2.Compiler[F, F] // for fs2.Stream
) extends MissingPropsConverters
    with Matchers {

  private val personCreated: PropF[F] = forAllF { pp: (Person, Person) =>
    val p1 = pp._1
    val p2 = pp._2
    for {
      id1: PersonId <- dao.add(p1)
      id2: PersonId <- dao.add(p2)
    } yield {
      id1.value.nonEmpty && id2.value.nonEmpty :| "create person with ID" &&
      (id1 =!= id2) :| "IDs are unique"
    }
  }

  private val personRead: PropF[F] = forAllF { pp: (Person, Person) =>
    val p1 = pp._1
    val p2 = pp._2
    for {
      id1: PersonId     <- dao.add(p1)
      _                 <- dao.add(p2)
      p: Option[Person] <- dao.get(id1)
      n: Option[Person] <- dao.get(PersonId("X"))
    } yield {
      p.fold(false)(_ eqv p1) :| "read person correctly" &&
      n.isEmpty :| "don't read the person when the id is wrong"
    }
  }

  private val personUpdate: PropF[F] = forAllF { pp: (Person, Person) =>
    val p1 = pp._1
    val p2 = pp._2
    for {
      id1 <- dao.add(p1)
      r1  <- dao.update(id1, p2)
      r2  <- dao.update(PersonId("x"), p2)
      p   <- dao.get(id1)
    } yield {
      p.fold(false)(_ eqv p2) :| "update person correctly" &&
      (r1 eqv 1) :| "return number of updated records" &&
      (r2 eqv 0) :| "return 0 number for no updates"
    }
  }

  private val personDelete: PropF[F] = forAllF { pp: (Person, Person) =>
    val p1 = pp._1
    val p2 = pp._2
    for {
      id1 <- dao.add(p1)
      _   <- dao.add(p2)
      r1  <- dao.delete(id1)
      r2  <- dao.delete(PersonId("x"))
      p   <- dao.get(id1)
    } yield {
      p.isEmpty :| "person is deleted" &&
      (r1 eqv 1) :| "return number of deleted records" &&
      (r2 eqv 0) :| "return 0 number for no deleted records"
    }
  }

  private val peopleStreamed: PropF[F] = forAllF { pp: (Person, Person) =>
    val p1 = pp._1
    val p2 = pp._2
    for {
      _   <- dao.add(p1)
      _   <- dao.add(p2)
      res <- dao.all.compile.toList
    } yield {
      List(p1, p2).forall(res.map(_.person).contains) :| "person records are streamed"
    }
  }

  private val peopleStreamedOrdered: PropF[F] = forAllF { pp: (Person, Person) =>
    val p1 = pp._1
    val p2 = pp._2
    for {
      _   <- dao.add(p1)
      _   <- dao.add(p2)
      res <- dao.allOrderByJoined.compile.toList
    } yield {
      List(p1, p2).forall(res.map(_.person).contains) :| "person records are streamed" &&
      (res.head.person.joined.toEpochMilli < res.last.person.joined.toEpochMilli) :| "stream of person is ordered"
    }
  }

  private val allProperties: List[PropF[F]] = List(
    personCreated,
    personRead,
    personUpdate,
    personDelete,
    peopleStreamed,
    peopleStreamedOrdered
  )

  private val allResults: F[List[Test.Result]] = allProperties.traverse(_.check())

  def run: F[Unit] = {
    allResults.flatMap { results: List[Test.Result] =>
      val errors: List[String] = results.filterNot(_.passed).map(f => pretty(f))
      MonadThrow[F].raiseWhen(errors.nonEmpty)(new RuntimeException(errors.mkString("; ")))
    }
  }

}
