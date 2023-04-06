package ru.psuti.apache1337.homeowners.data.splash.net

import retrofit2.Response
import retrofit2.http.GET

interface SplashApiService {
    @GET("version")
    suspend fun getVersion(): Response<String>
}