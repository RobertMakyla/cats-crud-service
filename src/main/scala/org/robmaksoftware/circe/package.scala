package org.robmaksoftware

import io.circe.{Codec, Decoder, Encoder}
import org.robmaksoftware.domain.{Job, PersonId}

package object circe {

  implicit val personIdCodec: Codec[PersonId] = Codec.from(
    Decoder.decodeString.map(PersonId.apply),
    Encoder.encodeString.contramap(_.value)
  )

  private def deriveFromEnum[A](
      decoder: String ⇒ Either[String, A],
      encoder: A ⇒ String
  ): Codec[A] = Codec.from(
    Decoder.decodeString.emap(decoder),
    Encoder.encodeString.contramap(encoder)
  )

  implicit val jobCodec: Codec[Job] = deriveFromEnum(Job.decodeFromString, _.entryName)
}
