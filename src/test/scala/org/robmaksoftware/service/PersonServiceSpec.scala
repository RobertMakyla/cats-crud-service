package org.robmaksoftware.service

import java.time.Instant
import java.time.temporal.ChronoUnit

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import org.robmaksoftware.domain.{DateCredits, Person}
import org.robmaksoftware.domain.Sex.{Female, Male}
import org.robmaksoftware.repo.Repo
import org.scalatest.FutureOutcome
import org.scalatest.freespec.FixtureAsyncFreeSpec
import org.scalatest.matchers.should.Matchers

class PersonServiceSpec extends FixtureAsyncFreeSpec with AsyncIOSpec /*for IO asserting*/ with Matchers {


  "PersonService should" - {

    val date = Instant.ofEpochMilli(12345)
    val nextDate = date.plus(1, ChronoUnit.DAYS)

    val p1 = Person("Robert", 37, Male, 10L, date)
    val p2 = Person("Jane", 38, Female, 20L, nextDate.plusMillis(123))
    val p3 = Person("Mary", 25, Female, 30L, nextDate.plusMillis(456))


    "add" in { service =>
      service.add(p1).asserting(_.value.nonEmpty shouldBe true)
    }

    "count" in { service =>
      val res = for {
        _ <- service.add(p1)
        _ <- service.add(p2)
        _ <- service.add(p3)
        c <- service.count
      } yield c

      res.asserting(_ shouldBe 3)
    }

    "sum credits" in { service =>
      val res = for {
        _ <- service.add(p1)
        _ <- service.add(p2)
        _ <- service.add(p3)
        sum <- service.sumCredits
      } yield sum

      res.asserting(_ shouldBe 60L)
    }

    "return credits per day" in { service =>
      val res: IO[List[DateCredits]] = for {
        _ <- service.add(p1)
        _ <- service.add(p2)
        _ <- service.add(p3)
        dateCredits <- service.creditsPerDate.compile.toList
      } yield dateCredits

      res.asserting {
        _ should contain theSameElementsAs
          List(
            DateCredits(date.truncatedTo(ChronoUnit.DAYS), 1, 10L),
            DateCredits(nextDate.truncatedTo(ChronoUnit.DAYS), 2, 50L)
          )
      }
    }
  }


  type FixtureParam = PersonService[IO]

  override def withFixture(test: OneArgAsyncTest): FutureOutcome = {
    val repo = Repo.inMem[IO]
    val service: PersonService[IO] = new PersonService[IO](repo)
    new FutureOutcome(test(service).toFuture)
  }
}