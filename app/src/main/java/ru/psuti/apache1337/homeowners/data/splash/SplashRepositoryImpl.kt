package ru.psuti.apache1337.homeowners.data.splash

import ru.psuti.apache1337.homeowners.data.splash.net.SplashApiService
import ru.psuti.apache1337.homeowners.domain.splash.SplashRepository
import javax.inject.Inject

class SplashRepositoryImpl @Inject constructor(private val splashApiService: SplashApiService) :
    SplashRepository {
    override suspend fun getBackendVersion(): String {
        return splashApiService.getVersion().body()!!
    }
}