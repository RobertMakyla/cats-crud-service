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

  private val arbitraryResponsibilitiesDeveloper: Gen[R.Developer] = goals.map(R.Developer)

  private val arbitraryResponsibilitiesArchitect: Gen[R.Architect] =
    for {
      roadmaps ← Gen.choose(1, 4)
      cert     ← Gen.oneOf("IBM", "AWS", "GCP").map(_ + " certificate")
    } yield R.Architect(
      roadmapsYearly = roadmaps,
      certificate    = cert
    )

  private val arbitraryOncallDeveloper: Gen[OC.Developer] = Gen.choose(0, 7).map(OC.Developer)
  private val arbitraryOncallArchitect: Gen[OC.Architect] = email.map(OC.Architect)

  private def arbitraryContractArchitect: Gen[Contract[Architect.type]] =
    for {
      resp   ← arbitraryResponsibilitiesArchitect
      oncall ← arbitraryOncallArchitect
      rate   ← Gen.choose(200, 300)
    } yield Contract[Architect.type](Architect, resp, oncall, rate)

  private def arbitraryContractDeveloper: Gen[Contract[Developer.type]] =
    for {
      resp   ← arbitraryResponsibilitiesDeveloper
      oncall ← arbitraryOncallDeveloper
      rate   ← Gen.choose(150, 250)
    } yield Contract[Developer.type](Developer, resp, oncall, rate)

  val arbitraryContract: Arbitrary[Contract[_ <: Job]] = Arbitrary(
    Gen.oneOf(arbitraryContractArchitect, arbitraryContractDeveloper)
  )

}
