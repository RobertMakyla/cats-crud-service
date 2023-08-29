package org.robmaksoftware.http

import cats.data.{Validated, ValidatedNel}
import org.robmaksoftware.domain.PersonId

trait Validators {

  def validateIsGreaterOrEqual(min: Int, value: Int): ValidatedNel[String, Int] = Validated.condNel(value >= min, value, s"$value < $min")

  def validateIsSmallerOrEqual(max: Int, value: Int): ValidatedNel[String, Int] = Validated.condNel(value <= max, value, s"$value > $max")

  def validateIsNonEmpty(id: => PersonId): ValidatedNel[String, PersonId] = Validated.condNel(id.value.nonEmpty, id, "ID is empty")
}
