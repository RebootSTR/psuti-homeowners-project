package ru.psuti.apache1337.homeowners.domain.counters.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CounterFilter(
    val filterDate: CounterDate,
    val filterType: CounterType,
    //val filterAddress: String
) : Parcelable
