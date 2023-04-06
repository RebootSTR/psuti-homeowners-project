package ru.psuti.apache1337.homeowners.data.counters.db.enitites

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import ru.psuti.apache1337.homeowners.domain.counters.model.Counter
import ru.psuti.apache1337.homeowners.domain.counters.model.CounterType

@ProvidedTypeConverter
class Converters {
    @TypeConverter
    fun counterTypeToString(type: CounterType): String {
        return when (type) {
            CounterType.GAS -> "GAS"
            CounterType.WATER_HOT -> "WATER_HOT"
            CounterType.WATER_COLD -> "WATER_COLD"
            CounterType.NONE -> "NONE"
            CounterType.ELECTRICITY -> "ELECTRICITY"
        }
    }

    @TypeConverter
    fun stringToCounterType(string: String): CounterType {
        return when (string) {
            "GAS" -> CounterType.GAS
            "WATER_HOT" -> CounterType.WATER_HOT
            "WATER_COLD" -> CounterType.WATER_COLD
            "NONE" -> CounterType.NONE
            "ELECTRICITY" ->  CounterType.ELECTRICITY
            else -> throw Exception("Invalid type string")
        }
    }

    @TypeConverter
    fun counterToString(counter: Counter): String {
        return "${counter.id},${counter.name},${counter.prev},${counter.alreadyUsed},${counterTypeToString(counter.type)},${counter.address}"
    }

    @TypeConverter
    fun stringToCounter(string: String): Counter {
        val list = string.split(",")

        return Counter(
            id = list[0].toInt(),
            name = list[1],
            prev = list[2].toDouble(),
            alreadyUsed = list[3].toBooleanStrict(),
            type = stringToCounterType(list[4]),
            address = list[4]
        )
    }
}