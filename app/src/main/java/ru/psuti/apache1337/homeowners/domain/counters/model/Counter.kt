package ru.psuti.apache1337.homeowners.domain.counters.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Counter(
    val id: Int,
    val name: String,
    val type: CounterType,
    var prev: Double? = null,
    var alreadyUsed: Boolean,
    val address: String
) : Parcelable
