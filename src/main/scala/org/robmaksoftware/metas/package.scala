package org.robmaksoftware

import doobie.util.meta.{LegacyInstantMetaInstance, Meta}
import org.robmaksoftware.domain.{PersonId, Sex}

package object metas extends LegacyInstantMetaInstance /*this gives Meta[Instant] */ {

  implicit val sexMeta: Meta[Sex] = Meta.StringMeta.imap(Sex.namesToValuesMap.apply)(_.entryName)

  implicit val personIdMeta: Meta[PersonId] = Meta.StringMeta.imap(PersonId.apply)(_.value)
}
