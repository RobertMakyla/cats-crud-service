package org.robmaksoftware.dao

import cats.effect.IO
import org.robmaksoftware.domain.{Person, PersonId}
import org.scalatest.{FutureOutcome, Outcome}

class PeopleDaoSpec  extends PeopleInMemDaoSpec {

  override def withFixture(test: OneArgAsyncTest): FutureOutcome = {
    val ioOutcome: IO[Outcome] = Dao.dbDao[IO].use { dao: Dao[IO, PersonId, Person] =>
      val testResult: FutureOutcome = withFixture(test.toNoArgAsyncTest(dao))
      IO.fromFuture(IO(testResult.toFuture))
    }
    new FutureOutcome(ioOutcome.unsafeToFuture())
  }
}
