package ru.psuti.apache1337.homeowners.domain.auth


interface AuthRepository {
    suspend fun getOneTimeCode(phone: String): Int
    suspend fun logIn(phone: String, code: Int)
    suspend fun createDemoUser()
}