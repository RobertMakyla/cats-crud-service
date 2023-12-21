package org.robmaksoftware.circe

import io.circe.syntax._
import org.robmaksoftware.domain.{
  Architect,
  ArchitectOncall,
  ArchitectResp,
  Developer,
  DeveloperOncall,
  DeveloperResp,
  Job,
  JobType,
  Oncall,
  PersonId,
  Responsibility
}
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

    val developerResp = DeveloperResp(List("Cert", "Course"))
    val architectResp = ArchitectResp(1, "GPC")
    "Responsibilities" in {
      test[Responsibility[_]](developerResp, "{\"DeveloperResp\":{\"goals\":[\"Cert\",\"Course\"]}}")
      test[Responsibility[_]](architectResp, "{\"ArchitectResp\":{\"roadmapsYearly\":1,\"certificate\":\"GPC\"}}")
    }

    val developerOncall = DeveloperOncall(daysPerWeek = 4)
    val architectOncall = ArchitectOncall("a@a.com")
    "Oncall" in {
      test[Oncall[_]](developerOncall, "{\"DeveloperOncall\":{\"daysPerWeek\":4}}")
      test[Oncall[_]](architectOncall, "{\"ArchitectOncall\":{\"email\":\"a@a.com\"}}")
    }

    "Job" in {
      test[Job[_]](
        Developer(developerResp, developerOncall),
        "{\"Developer\":{\"responsibility\":{\"goals\":[\"Cert\",\"Course\"]},\"oncall\":{\"daysPerWeek\":4}}}"
      )
      test[Job[_]](
        Architect(architectResp, architectOncall),
        "{\"Architect\":{\"responsibility\":{\"roadmapsYearly\":1,\"certificate\":\"GPC\"},\"oncall\":{\"email\":\"a@a.com\"}}}"
      )
    }

    // todo contract

  }

  def test[A](instance: A, encoded: String)(implicit ev: Codec[A]) = {
    // Encode into Json
    val jsonStr: String = instance.asJson.noSpaces
    jsonStr shouldBe encoded

    // Decode the JSON string back to an Instance
    val decoded: Either[Error, A] = decode[A](encoded)
    decoded shouldBe Right(instance)
  }
}
