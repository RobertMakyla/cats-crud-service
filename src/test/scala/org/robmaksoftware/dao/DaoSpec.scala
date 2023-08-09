package org.robmaksoftware.dao

import java.time.Instant

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import cats.syntax.option._
import org.robmaksoftware.domain.{Person, PersonId}
import org.robmaksoftware.domain.Sex.{Female, Male}
import org.scalatest.FutureOutcome
import org.scalatest.freespec.FixtureAsyncFreeSpec
import org.scalatest.matchers.should.Matchers

class DaoSpec extends FixtureAsyncFreeSpec with AsyncIOSpec /*for IO asserting*/ with Matchers {


  "InMemory Dao should" - {

    val date = Instant.ofEpochMilli(12345)
    val p1 = Person("Robert", 37, Male, 10L, date)
    val p2 = Person("Jane", 38, Female, 10L, date.plusMillis(5))
    val p3 = Person("Mary", 25, Female, 10L, date.plusMillis(10))


    "Create" in { repo =>
      repo.add(p1).asserting(_.value.nonEmpty shouldBe true)
    }

    "Read" in { repo =>

      val result = for {
        id1 <- repo.add(p1)
        _ <- repo.add(p2)
        p <- repo.get(id1)
      } yield p

      result.asserting(_ shouldBe p1.some)
    }

    "Update" in { repo =>

      val result = for {
        id1 <- repo.add(p1)
        _ <- repo.add(p2)
        _ <- repo.update(id1, p2)
        p <- repo.get(id1)
      } yield p

      result.asserting(_ shouldBe p2.some)
    }

    "Delete" in { repo =>

      val result = for {
        id1 <- repo.add(p1)
        _ <- repo.add(p2)
        _ <- repo.delete(id1)
        p <- repo.get(id1)
      } yield p

      result.asserting(_ shouldBe none)
    }


    "Stream all" in { repo =>

      val result: IO[List[Person]] = for {
        _ <- repo.add(p1)
        _ <- repo.add(p2)
        _ <- repo.add(p3)
        all <- repo.all.compile.toList
      } yield all

      result.asserting(_ should contain theSameElementsAs List(p1, p2, p3))
    }

    "Stream all order by joined" in { repo =>

      val result: IO[List[Person]] = for {
        _ <- repo.add(p2)
        _ <- repo.add(p1)
        _ <- repo.add(p3)
        all <- repo.allOrderByJoined.compile.toList
      } yield all

      result.asserting(_ shouldBe List(p1, p2, p3))
    }
  }

  type FixtureParam = Dao[IO, PersonId, Person]

  override def withFixture(test: OneArgAsyncTest): FutureOutcome = {
    val repo = Dao.inMemDao[IO]
    new FutureOutcome(test(repo).toFuture)
  }
}