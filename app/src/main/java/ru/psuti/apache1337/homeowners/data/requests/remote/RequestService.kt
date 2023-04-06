package ru.psuti.apache1337.homeowners.data.requests.remote

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import ru.psuti.apache1337.homeowners.data.requests.remote.dto.FileLoadResponseDTO
import ru.psuti.apache1337.homeowners.data.requests.remote.dto.RequestDTO
import ru.psuti.apache1337.homeowners.data.requests.remote.dto.RequestEditDTO


interface RequestService {
    @POST("/rest/request")
    suspend fun sendRequest(
        @Body request: RequestDTO
    ): Response<RequestDTO>

    @POST("/rest/request/{id}")
    suspend fun editRequest(
        @Path("id") requestId: Int,
        @Body request: RequestEditDTO
    ): Response<RequestDTO>

    @GET("/rest/request")
    suspend fun getHistory(
        @Query("clientId") id: Int,
    ) : Response<List<RequestDTO>>

    @Multipart
    @POST("/rest/files")
    suspend fun loadFile(
        @Part() filePart: MultipartBody.Part
    ): Response<FileLoadResponseDTO>

    @GET("/rest/files/{filename}")
    suspend fun getFile(
        @Path("filename") filename: String
    ): Response<ResponseBody>

}