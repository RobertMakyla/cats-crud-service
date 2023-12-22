package org.robmaksoftware.circe

import io.circe.testing.golden.GoldenCodecTests
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.prop.Configuration
import org.typelevel.discipline.scalatest.FlatSpecDiscipline
import io.circe.testing.ArbitraryInstances

class CirceGoldenSpec extends AnyFlatSpec
  with FlatSpecDiscipline
  with Configuration
  with ArbitraryInstances /* gives Arbitrary[Json] */ {

  import org.robmaksoftware.domain.{Contract, JobType, ContractArch, ContractDev}
  import org.robmaksoftware.domain.Contract._ // Eq
  import org.robmaksoftware.arbitrary.ContractGenerator._ // Arbitrary

  checkAll("GoldenCodec[Contract[JobType]]", GoldenCodecTests[Contract[JobType]].goldenCodec)
  checkAll("GoldenCodec[ContractArch]", GoldenCodecTests[ContractArch].goldenCodec)
  checkAll("GoldenCodec[ContractDev]", GoldenCodecTests[ContractDev].goldenCodec)
}