package org.robmaksoftware.dao

import java.time.Instant

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import cats.syntax.option._
import org.robmaksoftware.domain.{PersonWithId, Person, PersonId}
import org.robmaksoftware.domain.Sex.{Female, Male}
import org.scalatest.freespec.FixtureAsyncFreeSpec
import org.scalatest.matchers.should.Matchers

abstract class PeopleDaoSpec extends FixtureAsyncFreeSpec with AsyncIOSpec /*for IO asserting*/ with Matchers {


  "PeopleDao should" - {

    "Create with unique ID" in { repo =>

      val result = for {
        id1 <- repo.add(p1)
        id2 <- repo.add(p1)
      } yield (id1, id2)

      result.asserting { res =>
        val (resId1, resId2) = res
        resId1.value should not be empty
        resId2.value should not be empty
        resId1 should not be resId2
      }
    }

    "Read" in { repo =>

      val result = for {
        id1 <- repo.add(p1)
        _ <- repo.add(p2)
        p <- repo.get(id1)
        n <- repo.get(PersonId("X"))
      } yield (p, n)

      result.asserting { res =>
        val (personOpt, noPerson) = res
        personOpt shouldBe p1.some
        noPerson shouldBe none
      }
    }

    "Update existing Person" in { repo =>

      val result = for {
        id1 <- repo.add(p1)
        i <- repo.update(id1, p2)
        p <- repo.get(id1)
      } yield (i, p)

      result.asserting { res =>
        val (inserted, personOpt) = res
        inserted shouldBe 1
        personOpt shouldBe p2.some
      }
    }

    "Update non-existing Person" in { repo =>

      val result = for {
        id1 <- repo.add(p1)
        i <- repo.update(PersonId("X"), p2)
        p <- repo.get(id1)
      } yield (i, p)

      result.asserting { res =>
        val (inserted, personOpt) = res
        inserted shouldBe 0
        personOpt shouldBe p1.some

      }
    }

    "Delete" in { repo =>

      val result = for {
        id1 <- repo.add(p1)
        _ <- repo.add(p2)
        i <- repo.delete(id1)
        n <- repo.get(id1)
      } yield (i, n)

      result.asserting { res =>
        val (deleted, noPerson) = res
        deleted shouldBe 1
        noPerson shouldBe none
      }
    }


    "Stream all" in { repo =>

      val result: IO[List[PersonWithId]] = for {
        _ <- repo.add(p1)
        _ <- repo.add(p2)
        _ <- repo.add(p3)
        all <- repo.all.compile.toList
      } yield all

      result.asserting(_.map(_.person) should contain allElementsOf expectedRecords)
    }

    "Stream all order by joined" in { repo =>

      val result: IO[List[PersonWithId]] = for {
        _ <- repo.add(p2)
        _ <- repo.add(p1)
        _ <- repo.add(p3)
        all <- repo.allOrderByJoined.compile.toList
      } yield all

      result.asserting(_.map(_.person).filter(expectedRecords.contains) shouldBe expectedRecords)
    }

  }

  val date = Instant.ofEpochMilli(12345)

  val p1 = Person("Robert", 37, Male, 10L, date)
  val p2 = Person("Jane", 38, Female, 10L, date.plusMillis(5))
  val p3 = Person("Mary", 25, Female, 10L, date.plusMillis(10))

  val expectedRecords = List(p1, p2, p3)


  override type FixtureParam = Dao[IO, PersonId, Person, PersonWithId]

}