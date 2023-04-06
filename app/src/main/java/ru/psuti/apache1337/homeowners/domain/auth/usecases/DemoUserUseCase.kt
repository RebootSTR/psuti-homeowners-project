package ru.psuti.apache1337.homeowners.domain.auth.usecases

import ru.psuti.apache1337.homeowners.domain.auth.AuthRepository
import javax.inject.Inject

class DemoUserUseCase @Inject constructor(val authRepository: AuthRepository) {
    suspend fun createDemoUser() {
        authRepository.createDemoUser()
    }
}