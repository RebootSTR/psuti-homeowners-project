package ru.psuti.apache1337.homeowners.domain.registration.usecases

import ru.psuti.apache1337.homeowners.domain.registration.RegistrationMapper
import ru.psuti.apache1337.homeowners.domain.registration.RegistrationRepository
import ru.psuti.apache1337.homeowners.domain.registration.model.User
import javax.inject.Inject

class RegistrationUseCase @Inject constructor(
    private val registrationRepository: RegistrationRepository,
) {
    suspend fun registration(user: User, code: Int) {
        registrationRepository.registration(RegistrationMapper.map(user, code))
    }
}