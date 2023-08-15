package org.robmaksoftware.dao

import cats.Eq
import cats.effect.{Async, IO}
import cats.effect.kernel.Resource
import cats.effect.testing.scalatest.AsyncIOSpec
import org.robmaksoftware.domain.Person
import org.scalacheck.Arbitrary
import org.scalatest.freespec.AsyncFreeSpec

class PeopleDaoGenSpec extends AsyncFreeSpec with AsyncIOSpec {

  import PeopleDaoGenSpec._

    "property based testing" in {
      import org.robmaksoftware.arbitrary.PeopleGenerator._

      propsResource[IO].use(_.run)
    }
}

object PeopleDaoGenSpec {

  def propsResource[F[_] : Async](
    implicit resultArb: Arbitrary[(Person, Person)],
    personEq: Eq[Person]
  ): Resource[F, PeopleDaoGenProps[F]] =
    for {
      dao <- Dao.dbDao[F]
    } yield new PeopleDaoGenProps[F](dao)

}