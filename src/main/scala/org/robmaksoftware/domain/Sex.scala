package org.robmaksoftware.domain

import cats.Eq
import enumeratum.{Enum, EnumEntry}

sealed trait Sex extends EnumEntry

object Sex extends Enum[Sex] {

  case object Male extends Sex

  case object Female extends Sex

  override def values: IndexedSeq[Sex] = findValues

  implicit val sexEq: Eq[Sex] = Eq.by(_.entryName)
}