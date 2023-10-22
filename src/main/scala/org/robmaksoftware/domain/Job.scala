package org.robmaksoftware.domain

import cats.Eq
import enumeratum.{Enum, EnumEntry}
import contract.{responsibilities ⇒ R}
import contract.{oncall ⇒ OC}

sealed trait Job extends EnumEntry {
  type ResponsibilitiesType <: Responsibilities
  type OncallType           <: Oncall
}

object Job extends Enum[Job] {

  case object Architect extends Job {
    type ResponsibilitiesType = R.Architect
    type OncallType           = OC.Architect
  }

  case object Developer extends Job {
    type ResponsibilitiesType = R.Developer
    type OncallType           = OC.Developer
  }

  override def values: IndexedSeq[Job] = findValues

  implicit val jobEq: Eq[Sex] = Eq.by(_.entryName)
}
