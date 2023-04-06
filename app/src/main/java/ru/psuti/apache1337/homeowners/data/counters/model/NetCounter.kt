package ru.psuti.apache1337.homeowners.data.counters.model

data class NetCounter(
    val id: Int,
    val counter: CounterInfo,
    val dateTime: String,
    val value: Int
)