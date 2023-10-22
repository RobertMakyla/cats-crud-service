package org.robmaksoftware.domain

trait Job {
  type ResponsibilitiesType <: Responsibilities
  type OncallType <: Oncall
}

case object Architect extends Job

case object Developer extends Job

trait Responsibilities {
  val meetingsWeekly: Int
}

trait Oncall

package contract {

  package responsibilities {

    case class Developer(goals: List[String]) extends Responsibilities {
      override val meetingsWeekly: Int = 3
    }

    case class Architect(roadmapsYearly: Int, certificateType: String) extends Responsibilities {
      override val meetingsWeekly: Int = 1
    }
  }

  package oncall {
    case class Developer(daysPerWeek: Int) extends Oncall

    case class Architect(email: String) extends Oncall
  }

}

trait Contract[J <: Job] {
  def job: J
  def responsibilities: J#ResponsibilitiesType
  def oncall: J#OncallType
  def hourlyRateEur: Int
}

object Contract {
  def apply[J <: Job](j: J, resp: J#ResponsibilitiesType, oc: J#OncallType, rate: Int): Contract[J] = new Contract[J] {
    def job: J                                   = j
    def responsibilities: J#ResponsibilitiesType = resp
    def oncall: J#OncallType                     = oc
    def hourlyRateEur: Int                       = rate
  }
}
