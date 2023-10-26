package org.robmaksoftware

import io.circe.{Codec, Decoder, Encoder}
import org.robmaksoftware.domain.{
  ArchitectOncall,
  ArchitectResp,
  DeveloperOncall,
  DeveloperResp,
  Job,
  Oncall,
  PersonId,
  Responsibilities
}
import cats.syntax.functor._

package object circe {

  implicit val personIdCodec: Codec[PersonId] = Codec.from(
    Decoder.decodeString.map(PersonId.apply),
    Encoder.encodeString.contramap(_.value)
  )

  // Enums

  private def deriveFromEnum[A](
      decoder: String ⇒ Either[String, A],
      encoder: A ⇒ String
  ): Codec[A] = Codec.from(
    Decoder.decodeString.emap(decoder),
    Encoder.encodeString.contramap(encoder)
  )

  implicit val jobCodec: Codec[Job] = deriveFromEnum(Job.decodeFromString, _.entryName)

  // ADT - algebra data types (seal traits) https://circe.github.io/circe/codecs/adt.html

  import io.circe._
  import io.circe.generic.semiauto._
  import io.circe.syntax._

  private implicit val DeveloperOncallCodec: Codec[DeveloperOncall] = deriveCodec
  private implicit val ArchitectOncallCodec: Codec[ArchitectOncall] = deriveCodec

  implicit val OncallCodec: Codec[Oncall] = Codec.from(
    List[Decoder[Oncall]](
      Decoder[DeveloperOncall].widen,
      Decoder[ArchitectOncall].widen
    ).reduceLeft(_ or _),
    Encoder.instance {
      case d @ DeveloperOncall(_) ⇒ d.asJson
      case a @ ArchitectOncall(_) ⇒ a.asJson
    }
  )

  private implicit val DeveloperRespCodec: Codec[DeveloperResp] = deriveCodec
  private implicit val ArchitectRespCodec: Codec[ArchitectResp] = deriveCodec

  implicit val RespCodec: Codec[Responsibilities] = Codec.from(
    List[Decoder[Responsibilities]](
      Decoder[DeveloperResp].widen,
      Decoder[ArchitectResp].widen
    ).reduceLeft(_ or _),
    Encoder.instance {
      case d @ DeveloperResp(_)    ⇒ d.asJson
      case a @ ArchitectResp(_, _) ⇒ a.asJson
    }
  )

}
