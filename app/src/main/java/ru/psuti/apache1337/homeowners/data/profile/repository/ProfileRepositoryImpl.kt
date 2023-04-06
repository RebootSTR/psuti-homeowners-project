package ru.psuti.apache1337.homeowners.data.profile.repository

import androidx.lifecycle.LiveData
import ru.psuti.apache1337.homeowners.data.dto.Response
import ru.psuti.apache1337.homeowners.data.entities.User
import ru.psuti.apache1337.homeowners.data.profile.db.UserDao
import ru.psuti.apache1337.homeowners.data.profile.remote.ProfileService
import ru.psuti.apache1337.homeowners.data.profile.remote.dto.UpdateProfileDTO
import ru.psuti.apache1337.homeowners.data.profile.toEntity
import ru.psuti.apache1337.homeowners.domain.profile.repository.ProfileRepository
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val profileService: ProfileService
) : ProfileRepository {

    override fun getUserByNumber(number: String): LiveData<User> {
        return userDao.loadByNumber(number)
    }

    override suspend fun updateUser(profile: UpdateProfileDTO): Response {
        val result = profileService.updateProfile(profile)
        when (result.code()) {
            204 -> {
                updateUserInDB(profile.toEntity())
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

    private suspend fun updateUserInDB(user: User) {
        userDao.save(user)
    }

    override suspend fun logout(): Response {
        val result = profileService.logout()
        when (result.code()) {
            200 -> {
                return Response.Success(null)
            }
            else -> {
                return Response.Failure("Server answer is ${result.code()}")
            }
        }
    }
}