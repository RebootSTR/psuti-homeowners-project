package ru.psuti.apache1337.homeowners.domain.registration

import ru.psuti.apache1337.homeowners.data.registration.entities.RegistrationData
import ru.psuti.apache1337.homeowners.domain.registration.model.User

object RegistrationMapper {
    fun map(user: User, code: Int) = RegistrationData(
        phone = user.phoneNumber.replace("[() +]+".toRegex(), ""),
        email = user.email,
        firstName = user.firstName,
        lastName = user.lastName,
        middleName = user.middleName,
        city = user.city,
        street = user.street,
        house = user.house,
        building = user.korpus,
        apartment = user.apartment,
        code = code
    )
}