package org.robmaksoftware.dao

import cats.{Functor, MonadThrow}
import org.scalacheck.Prop
import org.scalacheck.effect.PropF
import org.scalacheck.effect.PropF.effectOfPropFToPropF

trait MissingPropsConverters {

  implicit def propToPropF[F[_]: MonadThrow](p: Prop): PropF[F] =
    PropF[F] { genParams =>
      val r: Prop.Result = p.apply(genParams)
      PropF.Result(r.status, r.args, r.collected, r.labels)
    }

  implicit def effectOfPropToPropF[F[_]: MonadThrow](fProp: F[Prop]): PropF[F] = {
    val res: F[PropF[F]] = Functor[F].map(fProp)(propToPropF[F])
    effectOfPropFToPropF(res)
  }
}
