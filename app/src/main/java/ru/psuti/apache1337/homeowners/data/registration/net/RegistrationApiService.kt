package ru.psuti.apache1337.homeowners.data.registration.net

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import ru.psuti.apache1337.homeowners.data.entities.ResponseUser
import ru.psuti.apache1337.homeowners.data.registration.entities.RegistrationRequest
import ru.psuti.apache1337.homeowners.data.registration.entities.ResponsePassword

interface RegistrationApiService {

    @FormUrlEncoded
    @POST("registration-otp")
    suspend fun postRegistrationOtp(@Field("username") phone: String): Response<ResponsePassword>

    @POST("rest/users")
    suspend fun postRegistration(@Body body: RegistrationRequest): Response<ResponseUser>
}