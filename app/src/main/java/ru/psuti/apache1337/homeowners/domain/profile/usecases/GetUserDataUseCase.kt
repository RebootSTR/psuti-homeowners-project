package ru.psuti.apache1337.homeowners.domain.profile.usecases

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ru.psuti.apache1337.homeowners.base.AppSharedPreferences
import ru.psuti.apache1337.homeowners.data.profile.toModel
import ru.psuti.apache1337.homeowners.domain.profile.model.ProfileModel
import ru.psuti.apache1337.homeowners.domain.profile.repository.ProfileRepository
import javax.inject.Inject

class GetUserDataUseCase @Inject constructor(
    private val repository: ProfileRepository,
    private val appSharedPreferences: AppSharedPreferences
) {
    fun execute(): LiveData<ProfileModel> {
        val number = appSharedPreferences.number.get()
        val profileFlow = repository.getUserByNumber(number)
        return profileFlow.map {
            if (it == null) {
                return@map ProfileModel(
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    ""
                )
            } else {
                it.toModel()
            }
        }
    }
}