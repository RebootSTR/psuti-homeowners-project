package ru.psuti.apache1337.homeowners.data.registration

import ru.psuti.apache1337.homeowners.data.registration.entities.RegistrationRequest
import ru.psuti.apache1337.homeowners.data.registration.entities.RegistrationData

object RegistrationMapper {
    fun map(user: RegistrationData) = RegistrationRequest(
        phone = user.phone,
        email = user.email,
        firstName = user.firstName,
        secondName = user.lastName,
        patronymic = user.middleName,
        city = user.city,
        street = user.street,
        house = user.house.toString(),
        building = user.building,
        apartment = user.apartment.toString(),
        otp = user.code.toString(),
    )
}
