package ru.psuti.apache1337.homeowners.domain.registration.model

data class User(
    val lastName: String,
    val firstName: String,
    val middleName: String,
    val phoneNumber: String,
    val email: String,
    val city: String,
    val street: String,
    val house: Int,
    val korpus: String,
    val apartment: Int
)