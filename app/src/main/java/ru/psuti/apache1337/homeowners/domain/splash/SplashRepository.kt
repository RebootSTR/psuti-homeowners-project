package ru.psuti.apache1337.homeowners.domain.splash

interface SplashRepository {
    suspend fun getBackendVersion(): String
}