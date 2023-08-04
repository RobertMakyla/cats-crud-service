package org.robmaksoftware.repo

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import cats.syntax.option._
import org.robmaksoftware.domain.Person
import org.robmaksoftware.domain.Sex.{Female, Male}
import org.scalatest.FutureOutcome
import org.scalatest.freespec.FixtureAsyncFreeSpec
import org.scalatest.matchers.should.Matchers

class RepoSpec extends FixtureAsyncFreeSpec with AsyncIOSpec with Matchers {


  "InMemory Repo should" - {

    val p1 = Person("Robert", 37, Male)
    val p2 = Person("Jane", 38, Female)


    "Create" in { repo =>
      repo.add(p1).asserting(_.value.nonEmpty shouldBe true)
    }

    "Read" in { repo =>

      val result = for {
        id1 <- repo.add(p1)
        p <- repo.get(id1)
      } yield p

      result.asserting(_ shouldBe p1.some)
    }

    "Update" in { repo =>

      val result = for {
        id1 <- repo.add(p1)
        _ <- repo.update(id1, p2)
        p <- repo.get(id1)
      } yield p

      result.asserting(_ shouldBe p2.some)
    }

    "Delete" in { repo =>

      val result = for {
        id1 <- repo.add(p1)
        _ <- repo.delete(id1)
        p <- repo.get(id1)
      } yield p

      result.asserting(_ shouldBe none)
    }
  }

  type FixtureParam = RepoInMem[IO]

  override def withFixture(test: OneArgAsyncTest): FutureOutcome = {
    val repo = Repo.inMem[IO]
    new FutureOutcome(test(repo).toFuture)
  }
}