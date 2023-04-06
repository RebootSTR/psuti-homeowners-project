package ru.psuti.apache1337.homeowners.data.auth

import ru.psuti.apache1337.homeowners.base.AppSharedPreferences
import ru.psuti.apache1337.homeowners.data.auth.db.AuthDao
import ru.psuti.apache1337.homeowners.data.auth.net.AuthApiService
import ru.psuti.apache1337.homeowners.data.entities.User
import ru.psuti.apache1337.homeowners.domain.auth.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val authDao: AuthDao,
    private val appSharedPreferences: AppSharedPreferences
) :
    AuthRepository {
    override suspend fun getOneTimeCode(phone: String): Int {
        val data = authApiService.postOneTimeCode(phone)
        when (data.code()) {
            200 -> return data.body()?.smsPassword!!
            400 -> throw IllegalArgumentException("Phone wasn't valid")
            404 -> throw IllegalArgumentException("Phone didn't found")
            else -> throw Exception("Unknown error")
        }
    }

    override suspend fun logIn(phone: String, code: Int) {
        val data = authApiService.postAuth(phone, code)
        when (data.code()) {
            200 -> {
                val user = data.body()!!
                authDao.save(AuthMapper.map(user))
                appSharedPreferences.userId.set(user.id)
                appSharedPreferences.number.set(user.phone)
            }
            400 -> throw IllegalArgumentException("User wasn't valid")
            401 -> throw IllegalArgumentException("Password wasn't valid")
            404 -> throw IllegalArgumentException("User didn't found")
            else -> throw Exception("Unknown error")
        }
    }

    override suspend fun createDemoUser() {
        val user = User(
            id = 0,
            phone = "79555555555",
            email = "email@mail.com",
            firstName = "Мария",
            secondName = "Соловьева",
            patronymic = "Руслановна",
            city = "Москва",
            street = "Красная Пресня",
            house = 44,
            building = "1",
            apartment = 42
        )
        authDao.save(user)
        appSharedPreferences.userId.set(user.id)
        appSharedPreferences.number.set(user.phone)
    }
}