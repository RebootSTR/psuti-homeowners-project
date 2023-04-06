package ru.psuti.apache1337.homeowners.domain.usecases

import ru.psuti.apache1337.homeowners.domain.auth.AuthRepository
import javax.inject.Inject

class AutoLoginUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend fun autoLogin(phone: String) {
        val newPhone = phone.replace("[() +]+".toRegex(), "")
        val code = authRepository.getOneTimeCode(newPhone)
        authRepository.logIn(newPhone, code)
    }
}