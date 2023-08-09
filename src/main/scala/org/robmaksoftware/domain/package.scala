package org.robmaksoftware

import java.time.Instant

import cats.Eq

package object domain {

  implicit val instantEq: Eq[Instant] = Eq.by(_.toEpochMilli)

}
