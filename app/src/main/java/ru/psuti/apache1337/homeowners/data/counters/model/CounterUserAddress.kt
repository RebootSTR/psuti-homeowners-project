package ru.psuti.apache1337.homeowners.data.counters.model

data class CounterUserAddress(
    val id: Int,
    val city: String,
    val street: String,
    val house: String,
    val building: String?,
    val apartment: String
)