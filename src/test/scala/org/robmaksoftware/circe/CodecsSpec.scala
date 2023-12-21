package org.robmaksoftware.circe

import io.circe.syntax._
import org.robmaksoftware.domain.{ArchitectOncall, ArchitectResp, Developer, DeveloperOncall, DeveloperResp, Job, JobType, Oncall, PersonId, Responsibility}
import org.robmaksoftware.domain.JobType.{Developer => JobTypeDev}
import org.robmaksoftware.domain.JobType.{Architect => JobTypeArch}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import io.circe._
import io.circe.parser._

class CodecsSpec extends AnyFreeSpec with Matchers {

  "decoding/encoding" - {

    "PersonId" in {
      test(PersonId("123"), "\"123\"")
    }
    "JobType" in {
      test[JobType](JobTypeArch, "\"Architect\"")
      test[JobType](JobTypeDev, "\"Developer\"")
    }
    "Responsibilities" in {
      test[Responsibility[_]](DeveloperResp(List("Cert", "Course")), "{\"goals\":[\"Cert\",\"Course\"]}")
      test[Responsibility[_]](ArchitectResp(1, "GPC"), "{\"roadmapsYearly\":1,\"certificate\":\"GPC\"}")
    }
    "Oncall" in {
      test[Oncall[_]](DeveloperOncall(daysPerWeek = 4) , "{\"daysPerWeek\":4}")
      test[Oncall[_]](ArchitectOncall("a@a.com") , "{\"email\":\"a@a.com\"}")
    }

    //todo job

    //todo contract

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
