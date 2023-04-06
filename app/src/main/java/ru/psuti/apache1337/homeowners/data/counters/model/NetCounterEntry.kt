package ru.psuti.apache1337.homeowners.data.counters.model

data class NetCounterEntry(
    val counter: NetCounterEntryCounter,
    val value: Int
)

data class NetCounterEntryCounter(
    val id: Int
)