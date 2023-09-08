package org.robmaksoftware.dao

import cats.effect.IO
import org.robmaksoftware.domain.{PersonWithId, Person, PersonId}
import org.scalatest.{FutureOutcome, Outcome}

class PeopleDaoInMemSpec extends PeopleDaoSpec {

  override def withFixture(test: OneArgAsyncTest): FutureOutcome = {
    val ioOutcome: IO[Outcome] = Dao.inMemDao[IO]().use { dao: Dao[IO, PersonId, Person, PersonWithId] ⇒
      val testResult: FutureOutcome = withFixture(test.toNoArgAsyncTest(dao))
      IO.fromFuture(IO(testResult.toFuture))
    }
    new FutureOutcome(ioOutcome.unsafeToFuture())
  }

  /*

  // when Fixture in not wrapped in Resource :

  override def withFixture(test: OneArgAsyncTest): FutureOutcome = {
    val repo = Dao.inMemDao[IO]
    new FutureOutcome(test(repo).toFuture)
  }

   */
}
