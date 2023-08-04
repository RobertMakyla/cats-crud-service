package org.robmaksoftware.repo

import cats.effect.IO
import cats.effect._
import cats.effect.testing.scalatest.AsyncIOSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.freespec.AsyncFreeSpec
//import cats.syntax.option._
//import cats.syntax.eq._
//import cats.Eq
import org.robmaksoftware.domain.{Person, PersonId}
import org.robmaksoftware.domain.Person._
import org.robmaksoftware.domain.Sex.{Female, Male}
import org.scalatest.matchers.should.Matchers

class RepoSpec extends AsyncFreeSpec with AsyncIOSpec with Matchers {

  "Mem Repo should" - {

    val repo = Repo.inMem[IO]

    val p1 = Person("Robert", 37, Male)
    val p2 = Person("Jane", 38, Female)


    "Create" in {
      repo.add(p1).asserting(_.value == "123")
    }

    //    "Read" in {
    //      for {
    //        id1 <- repo.add(p1)
    //        res <- repo.get(id1)
    //      } yield assert(res === p1)
    //    }

    //    // create
    //    val id1 = repo.add(p1)
    //
    //    // read
    //    repo.get(id1) shouldBe p1.some
    //
    //    //update
    //    repo.update(id1, p2)
    //    repo.get(id1) shouldBe p2.some
    //
    //    //delete
    //    repo.delete(id1)
    //    repo.get(id1) shouldBe none


  }
}