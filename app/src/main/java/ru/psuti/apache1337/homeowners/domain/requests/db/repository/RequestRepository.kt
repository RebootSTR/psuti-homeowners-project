package ru.psuti.apache1337.homeowners.domain.requests.db.repository

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import ru.psuti.apache1337.homeowners.data.dto.Response
import ru.psuti.apache1337.homeowners.data.requests.db.entity.RequestEntity
import ru.psuti.apache1337.homeowners.domain.requests.model.RequestModel
import java.io.File

interface RequestRepository {

    fun getRequestsHistory(userId: Int): LiveData<List<RequestEntity>>

    suspend fun sendRequest(request: RequestModel, userId: Int): Response

    suspend fun editRequest(request: RequestModel): Response

    suspend fun sendFile(file: MultipartBody.Part): Response

    suspend fun getFile(fileName: String): Response
}