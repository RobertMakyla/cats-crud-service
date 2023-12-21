package org.robmaksoftware

import io.circe.{Codec, Decoder, Encoder}
import org.robmaksoftware.domain.{ArchitectOncall, ArchitectResp, Contract, DeveloperOncall, DeveloperResp, Job, Oncall, PersonId, Responsibility}
import cats.syntax.functor._
import enumeratum.EnumEntry
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.deriveConfiguredCodec

package object circe {

  implicit val config: Configuration = Configuration.default

  implicit val personIdCodec: Codec[PersonId] = Codec.from(
    Decoder.decodeString.map(PersonId.apply),
    Encoder.encodeString.contramap(_.value)
  )

  // Enums - do not use deriveEnumerationCodec

  import io.circe.generic.extras.Configuration
  import io.circe.generic.extras.semiauto._
  import io.circe.syntax._

  def myDeriveFromEnum[A <: EnumEntry](
    decoder: String => Either[String, A],
    encoder: A => String
  ): Codec[A] = Codec.from(
    Decoder.decodeString.emap(decoder),
    Encoder.encodeString.contramap(encoder)
  )

  implicit val jobCodec: Codec[Job] = myDeriveFromEnum(
    str => Job.withNameEither(str).left.map(_.toString),
    _.entryName
  )

  // ADT - algebra data types (seal traits) https://circe.github.io/circe/codecs/adt.html

  implicit val responsiblityCodec: Codec[Responsibility] = deriveConfiguredCodec
//  private implicit val DeveloperRespCodec: Codec[DeveloperResp] = deriveConfiguredCodec
//  private implicit val ArchitectRespCodec: Codec[ArchitectResp] = deriveConfiguredCodec
//  implicit val RespCodec: Codec[Responsibility] = Codec.from( // I could use 'deriveCodec' but then coding would be uglier (with additional class name - too much info)
//    List[Decoder[Responsibility]](
//      Decoder[DeveloperResp].widen,
//      Decoder[ArchitectResp].widen
//    ).reduceLeft(_ or _),
//    Encoder.instance {
//      case d @ DeveloperResp(_)    ⇒ d.asJson
//      case a @ ArchitectResp(_, _) ⇒ a.asJson
//    }
//  )

  implicit val oncallCodec: Codec[Oncall] = deriveConfiguredCodec
//  private implicit val DeveloperOncallCodec: Codec[DeveloperOncall] = deriveConfiguredCodec
//  private implicit val ArchitectOncallCodec: Codec[ArchitectOncall] = deriveConfiguredCodec
//  implicit val OncallCodec: Codec[Oncall] = Codec.from( // I could use 'deriveCodec' but then coding would be uglier (with additional class name - too much info)
//    List[Decoder[Oncall]](
//      Decoder[DeveloperOncall].widen,
//      Decoder[ArchitectOncall].widen
//    ).reduceLeft(_ or _),
//    Encoder.instance {
//      case d @ DeveloperOncall(_) ⇒ d.asJson
//      case a @ ArchitectOncall(_) ⇒ a.asJson
//    }
//  )


 implicit val ContractCodec: Codec[Contract[Job]] = deriveConfiguredCodec

}
