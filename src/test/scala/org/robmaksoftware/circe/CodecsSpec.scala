package org.robmaksoftware.circe

import io.circe.syntax._
import org.robmaksoftware.domain.{
  ArchitectOncall,
  ArchitectResp,
  DeveloperOncall,
  DeveloperResp,
  Job,
  Oncall,
  PersonId,
  Responsibilities
}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import io.circe._
import io.circe.parser._

class CodecsSpec extends AnyFreeSpec with Matchers {

  "decoding/encoding" - {

    "PersonId" in {
      test(PersonId("123"), "\"123\"")
    }
    "Job" in {
      test(Job.Architect: Job, "\"Architect\"")
      test(Job.Developer: Job, "\"Developer\"")
    }
    "Oncall" in {
      test(DeveloperOncall(daysPerWeek = 4): Oncall, "{\"daysPerWeek\":4}")
      test(ArchitectOncall("a@a.com"): Oncall, "{\"email\":\"a@a.com\"}")
    }
    "Responsibilities" in {
      test(DeveloperResp(List("Cert", "Course")): Responsibilities, "{\"goals\":[\"Cert\",\"Course\"]}")
      test(ArchitectResp(1, "GPC"): Responsibilities, "{\"roadmapsYearly\":1,\"certificate\":\"GPC\"}")
    }

  }

  def test[A](instance: A, encoded: String)(implicit ev: Codec[A]) = {
    // Encode into Json
    val jsonStr: String = instance.asJson.noSpaces
    jsonStr shouldBe encoded

    // Decode the JSON string back to an Instance
    val decoded: Either[Error, A] = decode[A](jsonStr)
    decoded shouldBe Right(instance)
  }
}
