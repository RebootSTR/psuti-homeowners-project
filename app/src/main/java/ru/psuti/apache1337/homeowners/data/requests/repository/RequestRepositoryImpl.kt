package ru.psuti.apache1337.homeowners.data.requests.repository

import android.util.Log
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import ru.psuti.apache1337.homeowners.data.dto.Response
import ru.psuti.apache1337.homeowners.data.requests.db.dao.RequestDao
import ru.psuti.apache1337.homeowners.data.requests.db.entity.RequestEntity
import ru.psuti.apache1337.homeowners.data.requests.remote.RequestService
import ru.psuti.apache1337.homeowners.data.requests.toDTO
import ru.psuti.apache1337.homeowners.data.requests.toEditDTO
import ru.psuti.apache1337.homeowners.data.requests.toEntity
import ru.psuti.apache1337.homeowners.domain.requests.db.repository.RequestRepository
import ru.psuti.apache1337.homeowners.domain.requests.model.RequestModel
import java.util.*
import javax.inject.Inject

class RequestRepositoryImpl @Inject constructor(
    private val requestService: RequestService,
    private val requestDao: RequestDao
) : RequestRepository {

    override fun getRequestsHistory(userId: Int): LiveData<List<RequestEntity>> {
        CoroutineScope(Dispatchers.IO).launch {
            refreshRequestsHistory(userId)
        }
        return requestDao.loadByUserId(userId)
    }

    private suspend fun refreshRequestsHistory(userId: Int) {
        val historyExists = requestDao.hasRequests(userId, getMaxRefreshTime())
        if (!historyExists) {
            val data = requestService.getHistory(userId)
            when (data.code()) {
                200 -> {
                    data.body()?.let {
                        for (r in it.toEntity()) {
                            requestDao.save(r)
                        }
                    }
                }
                else -> Log.d("NET", "cookie lost")
            }
        }
    }

    override suspend fun sendRequest(request: RequestModel, userId: Int): Response {
        val result = requestService.sendRequest(request.toDTO(userId))
        when (result.code()) {
            200 -> {
                result.body()?.let {
                    requestDao.save(it.toEntity())
                }
                return Response.Success(null)
            }
            401 -> {
                return Response.Failure("Demo Mode")
            }
            else -> {
                return Response.Failure("Server answer is ${result.code()}")
            }
        }
    }

    override suspend fun editRequest(request: RequestModel): Response {
        val result = requestService.editRequest(request.id!!, request.toEditDTO())
        when (result.code()) {
            200 -> {
                result.body()?.let {
                    requestDao.save(it.toEntity())
                }
                return Response.Success(null)
            }
            401 -> {
                return Response.Failure("Demo Mode")
            }
            else -> {
                return Response.Failure("Server answer is ${result.code()}")
            }
        }
    }

    override suspend fun sendFile(file: MultipartBody.Part): Response {
        val result = requestService.loadFile(file)
        when (result.code()) {
            200 -> {
                return Response.Success(result.body())
            }
            401 -> {
                return Response.Failure("Demo Mode")
            }
            else -> {
                return Response.Failure("Server answer is ${result.code()}")
            }
        }
    }

    override suspend fun getFile(fileName: String): Response {
        try {
            val result = requestService.getFile(fileName)
            when (result.code()) {
                200 -> {
                    return Response.Success(result.body())
                }
                401 -> {
                    return Response.Failure("Demo Mode")
                }
                else -> {
                    return Response.Failure("Server answer is ${result.code()}")
                }
            }
        } catch (exception: Exception) {
            return Response.Failure(exception.message?: "Невозможное сообщение")
        }
    }

    private fun getMaxRefreshTime(): Date {
        val cal = Calendar.getInstance()
        cal.add(Calendar.MINUTE, -3);
        return cal.time;
    }

}