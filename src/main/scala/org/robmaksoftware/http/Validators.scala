package org.robmaksoftware.http

import cats.data.{Validated, ValidatedNel}
import org.robmaksoftware.domain.PersonId

trait Validators {

  def validateIsGreaterOrEqual(paramName: String, min: Int, value: Int): ValidatedNel[String, Int] =
    Validated.condNel(value >= min, value, s"$paramName is too small: $value < $min")

  def validateIsSmallerOrEqual(paramName: String, max: Int, value: Int): ValidatedNel[String, Int] =
    Validated.condNel(value <= max, value, s"$paramName is too big: $value > $max")

  def validateIsNonEmpty(paramName: String, id: => String): ValidatedNel[String, String] =
    Validated.condNel(id.nonEmpty, id, s"$paramName is empty")
}
