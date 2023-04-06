package ru.psuti.apache1337.homeowners.domain.registration


import ru.psuti.apache1337.homeowners.data.registration.entities.RegistrationData

interface RegistrationRepository {
    suspend fun registrationOTP(phone: String): Int
    suspend fun registration(user: RegistrationData)
}