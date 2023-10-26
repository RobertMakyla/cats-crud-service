package org.robmaksoftware.circe

import io.circe.syntax._
import org.robmaksoftware.domain.{Job, PersonId}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import io.circe._
import io.circe.parser._

class CodecsSpec extends AnyFreeSpec with Matchers {

  "decoding/encoding" - {
    "PersonId" in {

      val personId = PersonId("12345")

      // Encode into Json
      val jsonStr: String = personId.asJson.noSpaces

      jsonStr shouldBe "\"12345\""

      // Decode the JSON string back to an Instance
      val decoded: Either[Error, PersonId] = decode[PersonId](jsonStr)

      decoded shouldBe Right(personId)
    }

    "Job" in {
      val job: Job = Job.Architect

      // Encode into Json
      val jsonStr: String = job.asJson.noSpaces

      jsonStr shouldBe "\"Architect\""

      // Decode the JSON string back to an Instance
      val decoded: Either[Error, Job] = decode[Job](jsonStr)

      decoded shouldBe Right(job)
    }
  }
}
