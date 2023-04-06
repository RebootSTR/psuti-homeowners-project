package ru.psuti.apache1337.homeowners.domain.requests.usecases

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ru.psuti.apache1337.homeowners.base.AppSharedPreferences
import ru.psuti.apache1337.homeowners.data.requests.toModel
import ru.psuti.apache1337.homeowners.domain.profile.repository.ProfileRepository
import ru.psuti.apache1337.homeowners.domain.requests.db.repository.RequestRepository
import ru.psuti.apache1337.homeowners.domain.requests.model.RequestModel
import javax.inject.Inject

class GetHistoryRequestsUseCase @Inject constructor(
    private val requestRepository: RequestRepository,
    private val profileRepository: ProfileRepository,
    private val appSharedPreferences: AppSharedPreferences
) {
    fun execute(): LiveData<List<RequestModel>> {
        val userId = appSharedPreferences.userId.get()
        val data = requestRepository.getRequestsHistory(userId)
        return data.map { it.toModel() }
    }
}