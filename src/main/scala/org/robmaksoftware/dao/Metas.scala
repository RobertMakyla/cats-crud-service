package org.robmaksoftware.dao

import doobie.util.meta.{LegacyInstantMetaInstance, Meta}
import org.robmaksoftware.domain.{PersonId, Sex}

object Metas extends LegacyInstantMetaInstance /*this gives Meta[Instant] */ {

  implicit val sexMeta: Meta[Sex] = Meta.StringMeta.imap(Sex.namesToValuesMap.apply)(_.entryName)

  implicit val personIdMeta: Meta[PersonId] = Meta.StringMeta.imap(PersonId.apply)(_.value)
}
