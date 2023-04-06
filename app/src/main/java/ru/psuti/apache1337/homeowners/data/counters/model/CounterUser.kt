package ru.psuti.apache1337.homeowners.data.counters.model

data class CounterUser(
    val id: Int,
    val phone: String,
    val firstName: String,
    val secondName: String,
    val patronymic: String,
    val address: CounterUserAddress
)