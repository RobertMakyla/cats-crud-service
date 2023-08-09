package org.robmaksoftware.domain

import doobie.util.meta.{LegacyInstantMetaInstance, Meta}

object Metas extends LegacyInstantMetaInstance /*this gives Meta[Instant] */ {

  implicit val sexMeta: Meta[Sex] = Meta.StringMeta.imap(Sex.namesToValuesMap.apply)(_.entryName)

  implicit val personIdMeta: Meta[PersonId] = Meta.StringMeta.imap(PersonId.apply)(_.value)
}
