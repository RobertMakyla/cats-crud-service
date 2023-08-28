package org.robmaksoftware

import io.circe.Decoder
import io.circe.generic.semiauto._
import org.robmaksoftware.domain.PersonId

package object circe {

  implicit val personIdDecoder : Decoder[PersonId] = deriveDecoder
}
