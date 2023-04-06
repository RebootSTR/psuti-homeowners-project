package ru.psuti.apache1337.homeowners.data.auth

import ru.psuti.apache1337.homeowners.data.entities.ResponseUser
import ru.psuti.apache1337.homeowners.data.entities.User

object AuthMapper {
    fun map(responseUser: ResponseUser): User {
        return User(
            id = responseUser.id,
            phone = responseUser.phone,
            email = responseUser.email,
            firstName = responseUser.firstName,
            secondName = responseUser.secondName,
            patronymic = responseUser.patronymic,
            city = responseUser.city,
            street = responseUser.street,
            house = responseUser.house.toInt(),
            building = responseUser.building,
            apartment = responseUser.apartment.toInt()
        )
    }
}
