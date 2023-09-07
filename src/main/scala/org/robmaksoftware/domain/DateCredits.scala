package org.robmaksoftware.domain

import java.time.Instant

case class DateCredits(
    date: Instant,
    count: Int,
    credit: Double
)
