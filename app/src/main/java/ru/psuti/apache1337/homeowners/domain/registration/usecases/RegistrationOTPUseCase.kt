package ru.psuti.apache1337.homeowners.domain.registration.usecases

import ru.psuti.apache1337.homeowners.domain.registration.RegistrationRepository
import javax.inject.Inject

class RegistrationOTPUseCase @Inject constructor(
    private val registrationRepository: RegistrationRepository,
) {
    suspend fun getOneTimeCode(phone: String): Int {
        val newPhone = phone.replace("[() +]+".toRegex(), "")
        return registrationRepository.registrationOTP(newPhone)
    }
}