package org.robmaksoftware.arbitrary

import org.robmaksoftware.domain.{Architect, ArchitectOncall, ArchitectResp, Contract, Developer, DeveloperOncall, DeveloperResp, Job, JobType}
import org.robmaksoftware.domain.JobType.{Architect => JobTypeArc}
import org.robmaksoftware.domain.JobType.{Developer => JobTypeDev}
import org.scalacheck.{Arbitrary, Gen}

object ContractGenerator {

  private val name: Gen[String]  = Gen.stringOfN(5, Gen.alphaChar)
  private val email: Gen[String] = name.map(n => s"$n@gmail.com")

  private val goal: Gen[String] = for {
    aName <- name
    aType <- Gen.oneOf("Coursera certificate", "Udemy course", "Conference")
  } yield s"$aType $aName"

  private val goals = Gen.listOfN(3, goal)

  private val arbitraryResponsibilitiesDeveloper: Arbitrary[DeveloperResp] = Arbitrary { goals.map(DeveloperResp) }

  private val arbitraryResponsibilitiesArchitect: Arbitrary[ArchitectResp] = Arbitrary {
    for {
      roadmaps <- Gen.choose(1, 4)
      cert     <- Gen.oneOf("IBM", "AWS", "GCP").map(_ + " certificate")
    } yield ArchitectResp(
      roadmapsYearly = roadmaps,
      certificate    = cert
    )
  }

  private val arbitraryOncallDeveloper: Arbitrary[DeveloperOncall] = Arbitrary { Gen.choose(0, 7).map(DeveloperOncall) }
  private val arbitraryOncallArchitect: Arbitrary[ArchitectOncall] = Arbitrary { email.map(ArchitectOncall) }

  private val arbitraryArchitect: Arbitrary[Architect] = Arbitrary {
    for {
      resp   <- arbitraryResponsibilitiesArchitect.arbitrary
      oncall <- arbitraryOncallArchitect.arbitrary
    } yield Architect(resp, oncall)
  }

  private val arbitraryDeveloper: Arbitrary[Developer] = Arbitrary {
    for {
      resp   <- arbitraryResponsibilitiesDeveloper.arbitrary
      oncall <- arbitraryOncallDeveloper.arbitrary
    } yield Developer(resp, oncall)
  }

  private def arbitraryContractArchitect: Arbitrary[Contract[JobTypeArc.type]] = Arbitrary {
    for {
      job   <- arbitraryArchitect.arbitrary
      rate <- Gen.choose(250, 300)
    } yield Contract[JobTypeArc.type](JobType.Architect, job, rate)
  }

  private def arbitraryContractDeveloper: Arbitrary[Contract[JobTypeDev.type]] = Arbitrary {
    for {
      job   <- arbitraryDeveloper.arbitrary
      rate <- Gen.choose(200, 250)
    } yield Contract[JobTypeDev.type](JobType.Developer, job, rate)
  }

  val arbitraryContract: Arbitrary[Contract[JobType]] = Arbitrary {
    Gen.oneOf(
      arbitraryContractArchitect.arbitrary,
      arbitraryContractDeveloper.arbitrary
    )
  }

}
