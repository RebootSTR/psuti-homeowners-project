package ru.psuti.apache1337.homeowners.data.registration

import ru.psuti.apache1337.homeowners.data.registration.entities.RegistrationData
import ru.psuti.apache1337.homeowners.data.registration.net.RegistrationApiService
import ru.psuti.apache1337.homeowners.domain.registration.RegistrationRepository
import javax.inject.Inject

class RegistrationRepositoryImpl @Inject constructor(
    private val registrationApiService: RegistrationApiService,
) : RegistrationRepository {

    override suspend fun registrationOTP(phone: String): Int {
        val data = registrationApiService.postRegistrationOtp(phone)
        when (data.code()) {
            200 -> {
                return data.body()!!.smsPassword.toInt()
            }
            else -> throw Exception("Unknown error")
        }
    }

    override suspend fun registration(user: RegistrationData) {
        val data = registrationApiService.postRegistration(RegistrationMapper.map(user))
        when (data.code()) {
            201 -> {
                return
            }
            500 -> {
                throw IllegalArgumentException("Password wasn't valid")
            }
            else -> throw Exception("Unknown error")
        }
    }

}