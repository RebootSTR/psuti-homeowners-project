package ru.psuti.apache1337.homeowners.data.counters.db.enitites

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.psuti.apache1337.homeowners.domain.counters.model.CounterType

@Entity(tableName = "countersDemo")
data class DemoCounter(
    @PrimaryKey val id: Int,
    val name: String,
    val type: CounterType,
    val value: Double,
    val address: String,
)
