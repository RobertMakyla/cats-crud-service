package org.robmaksoftware.circe

import io.circe.testing.golden.GoldenCodecTests
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.prop.Configuration
import org.typelevel.discipline.scalatest.FlatSpecDiscipline
import io.circe.testing.ArbitraryInstances

class CirceGoldenSpec
    extends AnyFlatSpec
    with FlatSpecDiscipline
    with Configuration
    with ArbitraryInstances /* gives Arbitrary[Json] */ {

  import org.robmaksoftware.domain.{Contract, JobType, ContractArch, ContractDev}
  import org.robmaksoftware.domain.Contract._             // Eq
  import org.robmaksoftware.arbitrary.ContractGenerator._ // Arbitrary

  checkAll("GoldenCodec[Contract[JobType]]", GoldenCodecTests[Contract[JobType]](5).goldenCodec)
  checkAll("GoldenCodec[ContractArch]", GoldenCodecTests[ContractArch].goldenCodec)
  checkAll("GoldenCodec[ContractDev]", GoldenCodecTests[ContractDev].goldenCodec)

  /*
   Problem with adding new optional fields to classes:

   Let's say we add an optional field ContractDev(... , referal: Option[String])
   The problem is that GoldenCodecTest encodes each new optional field in JSON to 'referal = null'
   even if the Arbitrary[ContractDev] returns instances with referal = None
   so this will break our golden test.

   How to fix it ?

   Implement your own GoldenCodecTests where Encoder calls asJson().dropNullValues (or asJson().deepDropNullValues)
   on encoded JSON before comparing it with golden test file.

   Then adding optional fields will not break the circe golden tests,
   but we will be able to add new tests with new Arbitrary returning referal = Some(...)
   */
}
