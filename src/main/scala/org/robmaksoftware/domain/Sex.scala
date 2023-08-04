package org.robmaksoftware.domain

import enumeratum.{Enum, EnumEntry}

sealed trait Sex extends EnumEntry

object Sex extends Enum[Sex] {

  case object Male extends Sex

  case object Female extends Sex

  override val values: IndexedSeq[Sex] = findValues
}