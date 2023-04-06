package ru.psuti.apache1337.homeowners.data.profile

import ru.psuti.apache1337.homeowners.data.entities.User
import ru.psuti.apache1337.homeowners.data.profile.remote.dto.UpdateProfileDTO
import ru.psuti.apache1337.homeowners.data.toPhone
import ru.psuti.apache1337.homeowners.domain.profile.model.ProfileModel

fun User.toModel(): ProfileModel {
    return ProfileModel(
        this.secondName,
        firstName,
        this.patronymic,
        phone,
        this.email,
        city,
        street,
        house.toString(),
        building,
        apartment.toString()
    )
}

fun ProfileModel.toDTO(userId: Int): UpdateProfileDTO {
    return UpdateProfileDTO(
        userId,
        phone.toPhone(),
        email,
        firstName,
        lastName,
        middleName,
        city,
        street,
        house,
        building,
        room
    )
}

fun UpdateProfileDTO.toEntity(): User {
    return User(
        id,
        phone,
        email,
        firstName,
        secondName,
        patronymic,
        city,
        street,
        house.toInt(),
        building,
        apartment.toInt()
    )
}

