package org.robmaksoftware

import io.circe.{Codec, Decoder, Encoder}
import org.robmaksoftware.domain.{
  Architect,
  ArchitectOncall,
  ArchitectResp,
  Contract,
  Developer,
  DeveloperOncall,
  DeveloperResp,
  Job,
  JobType,
  Oncall,
  PersonId,
  Responsibility
}
import cats.syntax.functor._
import enumeratum.EnumEntry

package object circe {

  implicit val personIdCodec: Codec[PersonId] = Codec.from(
    Decoder.decodeString.map(PersonId.apply),
    Encoder.encodeString.contramap(_.value)
  )

  // Enums - do not use deriveEnumerationCodec

  def myDeriveFromEnum[A](
      decoder: String => Either[String, A],
      encoder: A => String
  ): Codec[A] = Codec.from(
    Decoder.decodeString.emap(decoder),
    Encoder.encodeString.contramap(encoder)
  )

  implicit val JobTypeCodec: Codec[JobType] = myDeriveFromEnum(
    str => JobType.withNameEither(str).left.map(_.toString),
    _.entryName
  )

  // ADT - algebra data types (seal traits) https://circe.github.io/circe/codecs/adt.html

  import io.circe.generic.extras.Configuration
  import io.circe.generic.extras.semiauto._
  import io.circe.syntax._

  implicit val config: Configuration = Configuration.default

  implicit val responsiblityCodec: Codec[Responsibility[_]]     = deriveConfiguredCodec
  private implicit val DeveloperRespCodec: Codec[DeveloperResp] = deriveConfiguredCodec
  private implicit val ArchitectRespCodec: Codec[ArchitectResp] = deriveConfiguredCodec
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

  implicit val oncallCodec: Codec[Oncall[_]]                        = deriveConfiguredCodec
  private implicit val DeveloperOncallCodec: Codec[DeveloperOncall] = deriveConfiguredCodec
  private implicit val ArchitectOncallCodec: Codec[ArchitectOncall] = deriveConfiguredCodec
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

  implicit val jobCodec: Codec[Job[_]]                     = deriveConfiguredCodec
  private implicit val DeveloperJobCodec: Codec[Developer] = deriveConfiguredCodec
  private implicit val ArchitectJobCodec: Codec[Architect] = deriveConfiguredCodec

//  implicit val ContractCodec: Codec[Contract[_]] = deriveConfiguredCodec

}
