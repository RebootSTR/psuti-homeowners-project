package ru.psuti.apache1337.homeowners.domain.requests.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.psuti.apache1337.homeowners.domain.profile.model.ProfileModel
import java.time.LocalDateTime
import java.util.*

@Parcelize
data class RequestModel(
    val id: Int? = null,
    val theme: String,
    val request: String,
    val date: LocalDateTime,
    val status: RequestStatus,
    val fileName: String
): Parcelable