package ru.psuti.apache1337.homeowners.data.counters.db.enitites

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.psuti.apache1337.homeowners.domain.counters.model.Counter
import ru.psuti.apache1337.homeowners.domain.counters.model.CounterType

@Entity(tableName = "history")
data class DbCounterHistoryItem(
    @PrimaryKey val id: Int,
    val counter: Counter,
    val value: Double,
    val dateTime: String,
    val address: String
)