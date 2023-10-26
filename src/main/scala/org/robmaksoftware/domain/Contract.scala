package org.robmaksoftware.domain

import cats.{Eq, Show}

trait Responsibilities { val meetingsWeekly: Int }

trait Oncall

case class DeveloperResp(goals: List[String]) extends Responsibilities {
  override val meetingsWeekly: Int = 3
}

case class ArchitectResp(roadmapsYearly: Int, certificate: String) extends Responsibilities {
  override val meetingsWeekly: Int = 1
}

case class DeveloperOncall(daysPerWeek: Int) extends Oncall

case class ArchitectOncall(email: String) extends Oncall

trait Contract[+J <: Job] {
  def job: J
  def responsibilities: J#ResponsibilitiesType
  def oncall: J#OncallType
  def hourlyRateEur: Int
}

object Contract {
  def apply[J <: Job](j: J, resp: J#ResponsibilitiesType, oc: J#OncallType, rate: Int): Contract[J] = new Contract[J] {
    def job: J = j

    def responsibilities: J#ResponsibilitiesType = resp

    def oncall: J#OncallType = oc

    def hourlyRateEur: Int = rate
  }

}
