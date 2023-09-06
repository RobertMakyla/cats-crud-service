package org.robmaksoftware.http

import java.time.Instant

import cats.effect.IO
import cats.syntax.option._
import cats.effect.testing.scalatest.AsyncIOSpec
import org.robmaksoftware.dao.Dao
import org.robmaksoftware.domain.Sex.{Female, Male}
import org.robmaksoftware.domain.{Person, PersonId, PersonWithId}
import org.robmaksoftware.service.PersonService
import org.scalatest.freespec.FixtureAsyncFreeSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{FutureOutcome, Outcome}
import org.robmaksoftware.http.{Resource => HttpResource}

import scala.collection.mutable.HashMap
import Converters._
import org.robmaksoftware.http.definitions.PeopleDto

class HandlerImplSpec extends FixtureAsyncFreeSpec with AsyncIOSpec /*for IO asserting*/ with Matchers {


  "getPerson" - {

    "returns Bad Request for empty ID" in { handler =>
      val res = handler.getPerson(HttpResource.GetPersonResponse)(PersonId(""))

      res.asserting {
        _ shouldBe HttpResource.GetPersonResponse.BadRequest("person ID is empty")
      }
    }

    "returns Not Found for nonexisting ID" in { handler =>
      val res = handler.getPerson(HttpResource.GetPersonResponse)(PersonId("x"))

      res.asserting {
        _ shouldBe HttpResource.GetPersonResponse.NotFound
      }
    }

    "returns OK for correct ID" in { handler =>
      val res = handler.getPerson(HttpResource.GetPersonResponse)(id(2))

      res.asserting {
        _ shouldBe HttpResource.GetPersonResponse.Ok(p2.toDtoWithId(id(2)))
      }
    }
  }

  "deletePerson" - {

    "returns Bad Request for empty ID" in { handler =>
      val res = handler.deletePerson(HttpResource.DeletePersonResponse)(PersonId(""))

      res.asserting {
        _ shouldBe HttpResource.DeletePersonResponse.BadRequest("person ID is empty")
      }
    }

    "returns Not Found for nonexisting ID" in { handler =>
      val res = handler.deletePerson(HttpResource.DeletePersonResponse)(PersonId("x"))

      res.asserting {
        _ shouldBe HttpResource.DeletePersonResponse.NotFound
      }
    }

    "returns OK for deleted entry" in { handler =>
      val res = handler.deletePerson(HttpResource.DeletePersonResponse)(id(2))

      res.asserting {
        _ shouldBe HttpResource.DeletePersonResponse.Ok
      }
    }
  }

  "getAllPeople" - {

    "returns Bad Request for incorrect offset" in { handler =>
      handler
        .getAllPeople(HttpResource.GetAllPeopleResponse)(offset = -1.some)
        .asserting(_ shouldBe HttpResource.GetAllPeopleResponse.BadRequest("offset is too small: -1 < 0"))
    }

    "returns Bad Request for incorrect limit" in { handler =>
      handler
        .getAllPeople(HttpResource.GetAllPeopleResponse)(limit = 999.some)
        .asserting(_ shouldBe HttpResource.GetAllPeopleResponse.BadRequest("limit is too big: 999 > 500"))
    }

    "returns Bad Request for all kinds of incorrect params" in { handler =>
      handler
        .getAllPeople(HttpResource.GetAllPeopleResponse)(offset = -1.some, limit = 999.some)
        .asserting(_ shouldBe HttpResource.GetAllPeopleResponse.BadRequest("offset is too small: -1 < 0; limit is too big: 999 > 500"))
    }

    "returns OK with pagination" in { handler =>
      handler
        .getAllPeople(HttpResource.GetAllPeopleResponse)(offset = 1.some, limit = 1.some)
        .asserting(_ shouldBe HttpResource.GetAllPeopleResponse.Ok(PeopleDto(List(
          p2.toDtoWithId(id(2))
        ))))
    }

    "returns OK without pagination" in { handler =>
      handler
        .getAllPeople(HttpResource.GetAllPeopleResponse)()
        .asserting(_ shouldBe HttpResource.GetAllPeopleResponse.Ok(PeopleDto(List(
          p1.toDtoWithId(id(1)),
          p2.toDtoWithId(id(2)),
          p3.toDtoWithId(id(3))
        ))))
    }
  }


  type FixtureParam = Handler[IO]

  override def withFixture(test: OneArgAsyncTest): FutureOutcome = {

    val data = HashMap(
      id(1) -> p1,
      id(2) -> p2,
      id(3) -> p3
    )
    val ioOutcome: IO[Outcome] = Dao.inMemDao[IO](data).use { dao: Dao[IO, PersonId, Person, PersonWithId] =>
      val testResult: FutureOutcome = withFixture(test.toNoArgAsyncTest(new HandlerImpl(PersonService.apply[IO](dao))))
      IO.fromFuture(IO(testResult.toFuture))
    }
    new FutureOutcome(ioOutcome.unsafeToFuture())
  }

  private val date = Instant.ofEpochMilli(12345)

  private def id(i: Int) = PersonId(s"id-$i")

  private val p1 = Person("Robert", 37, Male, 10L, date)
  private val p2 = Person("Jane", 38, Female, 20L, date.plusMillis(5))
  private val p3 = Person("Mary", 25, Female, 30L, date.plusMillis(10))

}