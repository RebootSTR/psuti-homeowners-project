package ru.psuti.apache1337.homeowners.domain.requests.usecases

import ru.psuti.apache1337.homeowners.base.AppSharedPreferences
import ru.psuti.apache1337.homeowners.data.dto.Response
import ru.psuti.apache1337.homeowners.domain.requests.db.repository.RequestRepository
import ru.psuti.apache1337.homeowners.domain.requests.model.RequestModel
import javax.inject.Inject

class SendRequestUseCase @Inject constructor(
    private val repository: RequestRepository,
    private val appSharedPreferences: AppSharedPreferences
) {
    suspend fun execute(param: RequestModel): Response {
        val userId = appSharedPreferences.userId.get()
        return repository.sendRequest(param, userId)
    }
}