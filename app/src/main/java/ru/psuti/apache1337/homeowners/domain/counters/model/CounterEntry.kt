package ru.psuti.apache1337.homeowners.domain.counters.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CounterEntry(
    val id: String,
    val counter: Counter,
    val current: String
) : Parcelable
