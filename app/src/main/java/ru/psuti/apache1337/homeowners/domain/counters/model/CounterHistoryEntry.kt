package ru.psuti.apache1337.homeowners.domain.counters.model

import java.time.Instant
import java.time.ZonedDateTime
import java.util.*

data class CounterHistoryEntry (
    val id: Int,
    val counter: Counter,
    val date: ZonedDateTime,
    val value: String,
    val address: String
    )