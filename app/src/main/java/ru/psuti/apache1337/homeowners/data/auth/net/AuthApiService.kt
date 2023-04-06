package ru.psuti.apache1337.homeowners.data.auth.net

import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import ru.psuti.apache1337.homeowners.data.entities.ResponseUser
import ru.psuti.apache1337.homeowners.data.auth.entities.User

interface AuthApiService {
    @FormUrlEncoded
    @POST("onetimecode")
    suspend fun postOneTimeCode(@Field("username") phone: String): Response<User>

    @FormUrlEncoded
    @POST("login")
    suspend fun postAuth(
        @Field("username") phone: String,
        @Field("password") password: Int
    ): Response<ResponseUser>
}