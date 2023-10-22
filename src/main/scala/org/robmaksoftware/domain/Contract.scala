package org.robmaksoftware.domain

import cats.{Eq, Show}

trait Responsibilities {
  val meetingsWeekly: Int
}

trait Oncall

trait Contract[+J <: Job] {
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

  implicit val show: Show[Contract[Job]] =
    Show.show(c ⇒ s"Contract(${c.job}, ${c.responsibilities}, ${c.oncall}, ${c.hourlyRateEur}) ")
  //  import io.circe._, io.circe.generic.semiauto._
  //
  //  implicit val decoderContract: Decoder[Contract] = deriveDecoder
  //  implicit val encoderContract: Encoder[Contract] = deriveEncoder
}

package contract {

  package responsibilities {

    case class Developer(goals: List[String]) extends Responsibilities {
      override val meetingsWeekly: Int = 3
    }

    case class Architect(roadmapsYearly: Int, certificate: String) extends Responsibilities {
      override val meetingsWeekly: Int = 1
    }
  }

  package oncall {
    case class Developer(daysPerWeek: Int) extends Oncall

    case class Architect(email: String) extends Oncall
  }

}
