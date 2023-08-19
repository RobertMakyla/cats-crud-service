package org.robmaksoftware.dao

import cats.{Eq, MonadThrow}
import cats.syntax.eq._
import cats.syntax.flatMap._
import cats.syntax.functor._
import cats.syntax.traverse._
import org.robmaksoftware.domain.Person
import org.robmaksoftware.domain.PersonId
import org.scalacheck.Prop._
import org.scalacheck.effect.PropF
import org.scalacheck.effect.PropF.forAllF
import org.scalacheck.util.Pretty.pretty
import org.scalacheck.{Arbitrary, Test}
import org.scalatest.matchers.should.Matchers

class PeopleDaoGenProps[F[_] : MonadThrow](
  dao: Dao[F, PersonId, Person]
)(
  implicit personArb: Arbitrary[(Person, Person)],
  personEq: Eq[Person],
  personIdEq: Eq[PersonId],
  compileEv: fs2.Compiler[F, F] // for fs2.Stream
) extends MissingPropsConverters with Matchers {

  private val personCreated: PropF[F] = forAllF{
    pp: (Person, Person) =>
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

  private val personRead: PropF[F] = forAllF{
    pp: (Person, Person) =>
      val p1 = pp._1
      val p2 = pp._2
      for {
        id1: PersonId <- dao.add(p1)
        _             <- dao.add(p2)
        p: Option[Person] <- dao.get(id1)
        n: Option[Person] <- dao.get(PersonId("X"))
      } yield {
        p.fold(false)(_ eqv p1) :| "read person correctly" &&
          n.isEmpty :| "don't read the person when the id is wrong"
      }
  }

  //todo more cases

  private val allProperties: List[PropF[F]] = List(
    personCreated,
    personRead,
  ) // todo complete the list

  private val allResults: F[List[Test.Result]] = allProperties.traverse(_.check())

  def run: F[Unit] = {
    allResults.flatMap {
      results: List[Test.Result] =>
        val errors: List[String] = results.filterNot(_.passed).map(f => pretty(f))
        MonadThrow[F].raiseWhen(errors.nonEmpty)(new RuntimeException(errors.mkString("; ")))
    }
  }

}
