package org.robmaksoftware.domain

import cats.Eq
import enumeratum.{Enum, EnumEntry}

//================================================= Responsibilities ==================================================

sealed trait Responsibility[+JT <: JobType] { val meetingsWeekly: Int }

final case class DeveloperResp(goals: List[String]) extends Responsibility[JobType.Developer.type] {
  override val meetingsWeekly: Int = 3
}

final case class ArchitectResp(roadmapsYearly: Int, certificate: String) extends Responsibility[JobType.Architect.type] {
  override val meetingsWeekly: Int = 1
}

//====================================================== Oncall =======================================================

sealed trait Oncall[+JT <: JobType]

final case class DeveloperOncall(daysPerWeek: Int) extends Oncall[JobType.Developer.type]

final case class ArchitectOncall(email: String) extends Oncall[JobType.Architect.type]

//===================================================== JobType =====================================================

sealed trait JobType extends EnumEntry

object JobType extends Enum[JobType] {
  case object Architect extends JobType
  case object Developer extends JobType

  override def values: IndexedSeq[JobType] = findValues
}

//===================================================== Job =====================================================

sealed trait Job[+JT <: JobType] {
  //  type ResponsibilitiesType <: Responsibilities // pain in circe
  //  type OncallType           <: Oncall
  def responsibility: Responsibility[JT]
  def oncall: Oncall[JT]
}

final case class Architect(responsibility: ArchitectResp, oncall: ArchitectOncall) extends Job[JobType.Architect.type]

final case class Developer(responsibility: DeveloperResp, oncall: DeveloperOncall) extends Job[JobType.Developer.type]

//===================================================== Contract =====================================================

sealed trait Contract[+JT <: JobType] { // Covariance '+J' is used so that Contract[Developer] is a subtype of Contract[JobType] - used in Generators
  def job: Job[JT]
  //  responsibilities: J#ResponsibilitiesType,  //PATH-DEPENDENT TYPES pain in circe (problems in scala 3)
  //  oncall: J#OncallType,
  def hourlyRateEur: Int
}

final case class ContractDev(job: Developer, hourlyRateEur: Int) extends Contract[JobType.Developer.type]
final case class ContractArch(job: Architect, hourlyRateEur: Int) extends Contract[JobType.Architect.type]

object Contract {
  implicit val contractEq: Eq[Contract[JobType]] = Eq.fromUniversalEquals[Contract[JobType]]
  implicit val contractDevEq: Eq[ContractDev] = Eq.fromUniversalEquals[ContractDev]
  implicit val contractArchEq: Eq[ContractArch] = Eq.fromUniversalEquals[ContractArch]
}