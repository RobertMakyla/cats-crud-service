package org.robmaksoftware.domain

import cats.Eq
import enumeratum.{Enum, EnumEntry}

sealed trait Job extends EnumEntry {
  type ResponsibilitiesType <: Responsibilities
  type OncallType           <: Oncall
}

object Job extends Enum[Job] {

  case object Architect extends Job {
    type ResponsibilitiesType = ArchitectResp
    type OncallType           = ArchitectOncall
  }

  case object Developer extends Job {
    type ResponsibilitiesType = DeveloperResp
    type OncallType           = DeveloperOncall
  }

  override def values: IndexedSeq[Job] = findValues

  implicit val jobEq: Eq[Sex] = Eq.by(_.entryName)
}
