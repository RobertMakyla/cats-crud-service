package org.robmaksoftware

import io.circe.{Decoder, Encoder}
import org.robmaksoftware.domain.PersonId

package object circe {

  implicit val personIdDecoder : Decoder[PersonId]  =  Decoder.decodeString.map(PersonId.apply)
  implicit val personIdEncoder: Encoder[PersonId] =  Encoder.encodeString.contramap(_.value)
}
