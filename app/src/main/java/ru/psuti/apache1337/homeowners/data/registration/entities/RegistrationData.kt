package ru.psuti.apache1337.homeowners.data.registration.entities

data class RegistrationData(
    val phone: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val middleName: String,
    val city: String,
    val street: String,
    val house: Int,
    val building: String,
    val apartment: Int,
    val code : Int
)