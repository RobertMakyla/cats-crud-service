package org.robmaksoftware.circe

import io.circe.syntax._
import org.robmaksoftware.domain.PersonId
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import io.circe._
import io.circe.parser._

class CodecsSpec extends AnyFreeSpec with Matchers {

  "decoding/encoding PersonId" in {

    val personId = PersonId("12345")

    // Encode the PersonId to a JSON string
    val jsonStr: String = personId.asJson.noSpaces

    jsonStr shouldBe "\"12345\""

    // Decode the JSON string back to a PersonId
    val decodedPersonId: Either[Error, PersonId] = decode[PersonId](jsonStr)

    decodedPersonId match {
      case Right(resultPersonId) => resultPersonId shouldBe personId
      case Left(error)           => fail(s"Decoding error: $error")
    }
  }
}
