package org.robmaksoftware.http

import java.time.Instant

import cats.data.{NonEmptyList, Validated, ValidatedNel}
import org.robmaksoftware.domain.Sex

import scala.util.Try

trait Validators {

  // Ints

  def validateRange(paramName: String, min: Int, max: Int, value: Int): ValidatedNel[String, Int] =
    validateIsGreaterOrEqual(paramName, min, value).andThen(_ ⇒ validateIsSmallerOrEqual(paramName, max, value))

  def validateIsGreaterOrEqual(paramName: String, min: Int, value: Int): ValidatedNel[String, Int] =
    Validated.condNel(value >= min, value, s"$paramName is too small: $value < $min")

  def validateIsSmallerOrEqual(paramName: String, max: Int, value: Int): ValidatedNel[String, Int] =
    Validated.condNel(value <= max, value, s"$paramName is too big: $value > $max")

  // String
  def validateIsNonEmpty(paramName: String, id: ⇒ String): ValidatedNel[String, String] =
    Validated.condNel(id.nonEmpty, id, s"$paramName is empty")

  // Sex
  def validateSex(s: ⇒ String): ValidatedNel[String, Sex] =
    Validated
      .fromOption(
        Sex.withNameInsensitiveOption(s),
        NonEmptyList(s"Sex $s is not ${Sex.values.toList.mkString("[", "; ", "]")}", Nil)
      )

  // Instant
  def validateInstant(paramName: String, value: Long): ValidatedNel[String, Instant] =
    Validated
      .fromTry(Try(Instant.ofEpochMilli(value)))
      .leftMap(ex ⇒ NonEmptyList(s"$paramName is incorrect: ${ex.getMessage}", Nil))

}
