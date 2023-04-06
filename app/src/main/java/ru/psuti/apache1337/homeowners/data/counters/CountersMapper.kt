package ru.psuti.apache1337.homeowners.data.counters

import ru.psuti.apache1337.homeowners.data.counters.db.enitites.*
import ru.psuti.apache1337.homeowners.data.counters.model.CounterUserAddress
import ru.psuti.apache1337.homeowners.data.counters.model.NetCounter
import ru.psuti.apache1337.homeowners.data.counters.model.NetCounterEntry
import ru.psuti.apache1337.homeowners.data.counters.model.NetCounterEntryCounter
import ru.psuti.apache1337.homeowners.data.entities.User
import ru.psuti.apache1337.homeowners.domain.counters.model.Counter
import ru.psuti.apache1337.homeowners.domain.counters.model.CounterEntry
import ru.psuti.apache1337.homeowners.domain.counters.model.CounterHistoryEntry
import ru.psuti.apache1337.homeowners.domain.counters.model.CounterType
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

object CountersMapper {
    fun demoToDomain(counter: DemoCounter): Counter {
        return Counter(
            id = counter.id,
            alreadyUsed = false,
            name = counter.name,
            type = counter.type,
            prev = counter.value,
            address = counter.address
        )
    }

    fun demoToDomainHistory(entry: DemoHistoryItem): CounterHistoryEntry {
        return CounterHistoryEntry(
            id = entry.id,
            counter = Counter(
                id = entry.counter.id,
                name = entry.counter.name,
                type = entry.counter.type,
                prev = entry.value,
                alreadyUsed = false,
                address = entry.address
            ),
            address = entry.address,
            date = mapDate(entry.dateTime),
            value = entry.value.toString()
        )
    }

    fun mapNetToDB(counter: NetCounter): DbCounter {
        return DbCounter(
            id = counter.counter.id,
            name = counter.counter.name,
            type = mapType(counter.counter.service.name),
            value = counter.value.toDouble(),
            address = mapAddress(counter.counter.user.address)
        )
    }

    fun mapDBToDomain(counter: DbCounter): Counter {
        return Counter(
            id = counter.id,
            alreadyUsed = false,
            name = counter.name,
            type = counter.type,
            prev = counter.value / 10,
            address = counter.address
        )
    }

    fun mapDbToDomainHistory(entry: DbCounterHistoryItem): CounterHistoryEntry {
        return CounterHistoryEntry(
            id = entry.id,
            counter = Counter(
                id = entry.counter.id,
                name = entry.counter.name,
                type = entry.counter.type,
                prev = entry.value / 10,
                alreadyUsed = false,
                address = entry.address
            ),
            address = entry.address,
            date = mapDate(entry.dateTime),
            value = entry.value.toString()
        )
    }

    fun mapNetToDBHistory(entry: NetCounter): DbCounterHistoryItem {
        return DbCounterHistoryItem(
            id = entry.id,
            counter = Counter(
                id = entry.counter.id,
                name = entry.counter.name,
                type = mapType(entry.counter.service.name),
                prev = entry.value.toDouble() / 10,
                alreadyUsed = false,
                address = mapAddress(entry.counter.user.address)
            ),
            address = mapAddress(entry.counter.user.address),
            dateTime = entry.dateTime,
            value = entry.value.toDouble()
        )
    }

    fun mapDomainToNet(entry: CounterEntry): NetCounterEntry {
        return NetCounterEntry(
            value = (entry.current.toDouble() * 10).toInt(),
            counter = NetCounterEntryCounter(id = entry.counter.id)
        )
    }

    fun mapDomainToDb(entry: CounterEntry): PendingCounterEntry {
        return PendingCounterEntry(
            id = entry.counter.id,
            name = entry.counter.name,
            value = entry.current.toDouble(),
            type = entry.counter.type
        )
    }

    fun mapDbToNet(entry: PendingCounterEntry): NetCounterEntry {
        return NetCounterEntry(
            counter = NetCounterEntryCounter(id = entry.id),
            value = (entry.value * 10).toInt()
        )
    }

    private fun mapAddress(address: CounterUserAddress): String {
        return if (address.building == null) "г. ${address.city} ул. ${address.street} д. ${address.house} кв. ${address.apartment}"
        else "г. ${address.city} ул. ${address.street} д. ${address.house} к/стр ${address.building} кв. ${address.apartment}"
    }

    fun mapUserToAddressString(user: User): String {
        return if (user.building == null) "г. ${user.city} ул. ${user.street} д. ${user.house} кв. ${user.apartment}"
        else "г. ${user.city} ул. ${user.street} д. ${user.house} к/стр ${user.building} кв. ${user.apartment}"
    }

    private fun mapDate(date: String): ZonedDateTime {
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ROOT)
        val tempDate = formatter.parse(date)!!
        return ZonedDateTime.ofInstant(
            tempDate.toInstant(),
            ZoneId.systemDefault()
        );
    }

    private fun mapType(type: String): CounterType {
        return when (type) {
            "Газ" -> CounterType.GAS
            "Холодная вода" -> CounterType.WATER_COLD
            "Горячая вода" -> CounterType.WATER_HOT
            "Электричество" -> CounterType.ELECTRICITY
            else -> CounterType.NONE
        }
    }
}