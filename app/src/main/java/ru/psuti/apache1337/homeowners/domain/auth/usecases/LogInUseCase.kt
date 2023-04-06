package ru.psuti.apache1337.homeowners.domain.auth.usecases

import ru.psuti.apache1337.homeowners.domain.auth.AuthRepository
import javax.inject.Inject

class LogInUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend fun logIn(phone: String, code: Int) {
        val newPhone = phone.replace("[() +]+".toRegex(), "")
        authRepository.logIn(newPhone, code)
    }
}