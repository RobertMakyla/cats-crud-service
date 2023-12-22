package org.robmaksoftware

import io.circe.{Codec, Decoder, Encoder}
import org.robmaksoftware.domain.{
  Architect,
  ArchitectOncall,
  ArchitectResp,
  Contract,
  ContractArch,
  ContractDev,
  Developer,
  DeveloperOncall,
  DeveloperResp,
  Job,
  JobType,
  Oncall,
  PersonId,
  Responsibility
}

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

  implicit val oncallCodec: Codec[Oncall[_]]                        = deriveConfiguredCodec
  private implicit val DeveloperOncallCodec: Codec[DeveloperOncall] = deriveConfiguredCodec
  private implicit val ArchitectOncallCodec: Codec[ArchitectOncall] = deriveConfiguredCodec

  implicit val jobCodec: Codec[Job[_]]                     = deriveConfiguredCodec
  private implicit val DeveloperJobCodec: Codec[Developer] = deriveConfiguredCodec
  private implicit val ArchitectJobCodec: Codec[Architect] = deriveConfiguredCodec

  implicit val ContractDevCodec: Codec[ContractDev]   = deriveConfiguredCodec
  implicit val ContractArchCodec: Codec[ContractArch] = deriveConfiguredCodec

  implicit val ContractCodec: Codec[Contract[JobType]] = deriveConfiguredCodec

}
