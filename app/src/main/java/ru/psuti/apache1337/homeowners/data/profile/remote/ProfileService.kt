package ru.psuti.apache1337.homeowners.data.profile.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import ru.psuti.apache1337.homeowners.data.profile.remote.dto.UpdateProfileDTO

interface ProfileService {

    @PUT("/rest/profile/update")
    suspend fun updateProfile(
        @Body profile: UpdateProfileDTO
    ): Response<String>

    @GET("/logout")
    suspend fun logout(): Response<String>

}