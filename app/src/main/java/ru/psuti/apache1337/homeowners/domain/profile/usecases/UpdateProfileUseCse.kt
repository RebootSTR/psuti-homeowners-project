package ru.psuti.apache1337.homeowners.domain.profile.usecases

import ru.psuti.apache1337.homeowners.base.AppSharedPreferences
import ru.psuti.apache1337.homeowners.data.dto.Response
import ru.psuti.apache1337.homeowners.data.profile.toDTO
import ru.psuti.apache1337.homeowners.domain.profile.model.ProfileModel
import ru.psuti.apache1337.homeowners.domain.profile.repository.ProfileRepository
import javax.inject.Inject

class UpdateProfileUseCse @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val appSharedPreferences: AppSharedPreferences,
) {

    suspend fun execute(profileModel: ProfileModel): Response {
        val userId = appSharedPreferences.userId.get()
        return profileRepository.updateUser(profileModel.toDTO(userId))
    }
}