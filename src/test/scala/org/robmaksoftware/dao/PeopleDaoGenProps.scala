package org.robmaksoftware.dao

import cats.{Eq, MonadThrow}
import cats.syntax.eq._
import cats.syntax.traverse._
import cats.syntax.flatMap._
import org.robmaksoftware.domain.{Person, PersonId}
import org.scalacheck.{Arbitrary, Test}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.effect.PropF
import org.scalacheck.Prop.propBoolean
import org.scalacheck.util.Pretty.pretty
import org.scalacheck.effect.PropF.forAllF
import org.scalatest.matchers.should.Matchers

class PeopleDaoGenProps[F[_] : MonadThrow](
  dao: Dao[F, PersonId, Person]
)(
  implicit personArb: Arbitrary[Person],
  personEq: Eq[Person],
  personIdEq: Eq[PersonId],
  compileEv: fs2.Compiler[F, F] // for fs2.Stream
) extends Matchers {

  private implicit val twoPeopleArb: Arbitrary[(Person, Person)] = Arbitrary {
    for {
      genP1 <- arbitrary[Person]
      genP2 <- arbitrary[Person]
    } yield (genP1, genP2)
  }

  private val personCreated: PropF[F] = forAllF {
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

  //todo more cases

  private val allProperties: List[PropF[F]] = List(personCreated) // todo complete the list

  private val allResults: F[List[Test.Result]] = allProperties.traverse(_.check())

  def run: F[Unit] = {
    allResults.flatMap {
      results: List[Test.Result] =>
        val errors: List[String] = results.filterNot(_.passed).map(f => pretty(f))
        MonadThrow[F].raiseWhen(errors.nonEmpty)(new RuntimeException(errors.mkString("; ")))
    }
  }

}
