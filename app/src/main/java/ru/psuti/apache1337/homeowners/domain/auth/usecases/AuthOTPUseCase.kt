package ru.psuti.apache1337.homeowners.domain.auth.usecases

import ru.psuti.apache1337.homeowners.domain.auth.AuthRepository

import javax.inject.Inject

class AuthOTPUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend fun getOneTimeCode(phone: String): Int {
        val newPhone = phone.replace("[() +]+".toRegex(), "")
        return authRepository.getOneTimeCode(newPhone)
    }
}