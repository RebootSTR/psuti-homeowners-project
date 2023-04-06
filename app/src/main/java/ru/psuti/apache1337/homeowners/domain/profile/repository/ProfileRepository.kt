package ru.psuti.apache1337.homeowners.domain.profile.repository

import androidx.lifecycle.LiveData
import ru.psuti.apache1337.homeowners.data.dto.Response
import ru.psuti.apache1337.homeowners.data.entities.User
import ru.psuti.apache1337.homeowners.data.profile.remote.dto.UpdateProfileDTO

interface ProfileRepository {

    fun getUserByNumber(number: String): LiveData<User>

    suspend fun updateUser(profile: UpdateProfileDTO): Response

    suspend fun logout(): Response
}