package ru.psuti.apache1337.homeowners.data.entities

data class ResponseUser(
    val id: Int,
    val phone: String,
    val email: String,
    val firstName: String,
    val secondName: String,
    val patronymic: String,
    val city: String,
    val street: String,
    val house: String,
    val building: String?,
    val apartment: String,
    val roles: String
)
