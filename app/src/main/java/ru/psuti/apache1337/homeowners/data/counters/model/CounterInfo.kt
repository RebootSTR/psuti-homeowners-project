package ru.psuti.apache1337.homeowners.data.counters.model

data class CounterInfo(
    val id: Int,
    val name: String,
    val user: CounterUser,
    val service: CounterService
)