package ru.psuti.apache1337.homeowners.domain.splash.usecases

import ru.psuti.apache1337.homeowners.domain.splash.SplashRepository
import javax.inject.Inject

class BackendVersionUseCase @Inject constructor(private val splashRepository: SplashRepository) {
    suspend fun getBackendVersion() = splashRepository.getBackendVersion()
}