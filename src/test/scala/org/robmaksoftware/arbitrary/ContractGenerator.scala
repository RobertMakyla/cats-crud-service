package org.robmaksoftware.arbitrary

import org.robmaksoftware.domain.{Contract, Job, contract}
import org.robmaksoftware.domain.Job.Architect
import org.robmaksoftware.domain.Job.Developer
import org.scalacheck.{Arbitrary, Gen}
import contract.{responsibilities ⇒ R}
import contract.{oncall ⇒ OC}

object ContractGenerator {

  private val name: Gen[String]  = Gen.stringOfN(5, Gen.alphaChar)
  private val email: Gen[String] = name.map(n ⇒ s"$n@gmail.com")

  private val goal: Gen[String] = for {
    aName ← name
    aType ← Gen.oneOf("Coursera certificate", "Udemy course", "Conference")
  } yield s"$aType $aName"

  private val goals = Gen.listOfN(3, goal)

  private val arbitraryResponsibilitiesDeveloper: Arbitrary[R.Developer] = Arbitrary { goals.map(R.Developer) }

  private val arbitraryResponsibilitiesArchitect: Arbitrary[R.Architect] = Arbitrary {
    for {
      roadmaps ← Gen.choose(1, 4)
      cert     ← Gen.oneOf("IBM", "AWS", "GCP").map(_ + " certificate")
    } yield R.Architect(
      roadmapsYearly = roadmaps,
      certificate    = cert
    )
  }

  private val arbitraryOncallDeveloper: Arbitrary[OC.Developer] = Arbitrary { Gen.choose(0, 7).map(OC.Developer) }
  private val arbitraryOncallArchitect: Arbitrary[OC.Architect] = Arbitrary { email.map(OC.Architect) }

  private def arbitraryContractArchitect: Arbitrary[Contract[Architect.type]] = Arbitrary {
    for {
      resp   ← arbitraryResponsibilitiesArchitect.arbitrary
      oncall ← arbitraryOncallArchitect.arbitrary
      rate   ← Gen.choose(200, 300)
    } yield Contract[Architect.type](Architect, resp, oncall, rate)
  }

  private def arbitraryContractDeveloper: Arbitrary[Contract[Developer.type]] = Arbitrary {
    for {
      resp   ← arbitraryResponsibilitiesDeveloper.arbitrary
      oncall ← arbitraryOncallDeveloper.arbitrary
      rate   ← Gen.choose(150, 250)
    } yield Contract[Developer.type](Developer, resp, oncall, rate)
  }

  val arbitraryContract: Arbitrary[Contract[Job]] = Arbitrary {
    Gen.oneOf(
      arbitraryContractArchitect.arbitrary,
      arbitraryContractDeveloper.arbitrary
    )
  }

}
