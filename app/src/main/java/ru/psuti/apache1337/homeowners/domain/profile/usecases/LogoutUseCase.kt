package ru.psuti.apache1337.homeowners.domain.profile.usecases

import ru.psuti.apache1337.homeowners.base.AppSharedPreferences
import ru.psuti.apache1337.homeowners.data.dto.Response
import ru.psuti.apache1337.homeowners.domain.profile.repository.ProfileRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val appSharedPreferences: AppSharedPreferences,
    private val profileRepository: ProfileRepository
) {
    suspend fun execute(): Response {
        val result = profileRepository.logout()
        when (result) {
            is Response.Success<*> -> {
                appSharedPreferences.userId.remove()
                appSharedPreferences.number.remove()
            }
            is Response.Failure -> {
            }
        }
        return result
    }
}
