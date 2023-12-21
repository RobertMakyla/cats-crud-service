package org.robmaksoftware.domain

import enumeratum.{Enum, EnumEntry}

//================================================= Responsibilities ==================================================

sealed trait Responsibility { val meetingsWeekly: Int }

final case class DeveloperResp(goals: List[String]) extends Responsibility {
  override val meetingsWeekly: Int = 3
}

final case class ArchitectResp(roadmapsYearly: Int, certificate: String) extends Responsibility {
  override val meetingsWeekly: Int = 1
}

//====================================================== Oncall =======================================================

sealed trait Oncall

final case class DeveloperOncall(daysPerWeek: Int) extends Oncall

final case class ArchitectOncall(email: String) extends Oncall

//===================================================== Job =====================================================

sealed trait Job extends EnumEntry {
  //  type ResponsibilitiesType <: Responsibilities // pain in circe
  //  type OncallType           <: Oncall
  def responsibility: Responsibility
  def oncall: Oncall
}

object Job extends Enum[Job] {
  final case class Architect(responsibility: ArchitectResp, oncall: ArchitectOncall) extends Job
  final case class Developer(responsibility: DeveloperResp, oncall: DeveloperOncall) extends Job

  override def values: IndexedSeq[Job] = findValues
}

//===================================================== Contract =====================================================

case class Contract[+J <: Job]( // Covariance '+J' is used so that Contract[Developer] is a subtype of Contract[Job] - used in Generators
    job: J,
//  responsibilities: J#ResponsibilitiesType,  //PATH-DEPENDENT TYPES pain in circe (problems in scala 3)
//  oncall: J#OncallType,
    hourlyRateEur: Int
)
