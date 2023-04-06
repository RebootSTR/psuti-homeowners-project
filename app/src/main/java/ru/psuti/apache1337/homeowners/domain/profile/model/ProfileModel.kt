package ru.psuti.apache1337.homeowners.domain.profile.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProfileModel(
    val lastName: String,
    val firstName: String,
    val middleName: String,
    val phone: String,
    val email: String,
    val city: String,
    val street: String,
    val house: String,
    val building: String?,
    val room: String
): Parcelable