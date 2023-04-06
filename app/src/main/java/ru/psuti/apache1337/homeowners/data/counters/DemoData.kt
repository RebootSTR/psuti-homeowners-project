package ru.psuti.apache1337.homeowners.data.counters

import ru.psuti.apache1337.homeowners.data.counters.db.enitites.DbCounter
import ru.psuti.apache1337.homeowners.data.counters.db.enitites.DemoCounter
import ru.psuti.apache1337.homeowners.data.counters.db.enitites.DemoHistoryItem
import ru.psuti.apache1337.homeowners.domain.counters.model.Counter
import ru.psuti.apache1337.homeowners.domain.counters.model.CounterType

object CountersDemoData {
    val counters = listOf(
        DemoCounter(
            id = 0,
            name = "Электричество",
            type = CounterType.ELECTRICITY,
            value = 300.0,
            address = ""
        ),
        DemoCounter(
            id = 1,
            name = "Холодная вода",
            type = CounterType.WATER_COLD,
            value = 300.0,
            address = ""
        ),
        DemoCounter(
            id = 2,
            name = "Горячая вода",
            type = CounterType.WATER_HOT,
            value = 300.0,
            address = ""
        ),
        DemoCounter(
            id = 3,
            name = "Газ",
            type = CounterType.GAS,
            value = 300.0,
            address = ""
        )
    )
    val history = listOf(
        DemoHistoryItem(
            id = 0,
            address = "",
            dateTime = "2020-09-06T17:30:45.123456",
            counter = Counter(
                id = 0,
                name = "Электричество",
                type = CounterType.ELECTRICITY,
                prev = 300.0,
                address = "",
                alreadyUsed = false
            ),
            value = 300.0
        ),
        DemoHistoryItem(
            id = 1,
            address = "",
            dateTime = "2021-04-06T17:30:45.123456",
            counter = Counter(
                id = 1,
                name = "Холодная вода",
                type = CounterType.WATER_COLD,
                prev = 300.0,
                address = "",
                alreadyUsed = false
            ),
            value = 300.0
        ),
        DemoHistoryItem(
            id = 2,
            address = "",
            dateTime = "2021-09-06T17:30:45.123456",
            counter = Counter(
                id = 2,
                name = "Горячая вода",
                type = CounterType.WATER_HOT,
                prev = 300.0,
                address = "",
                alreadyUsed = false
            ),
            value = 300.0
        ),
        DemoHistoryItem(
            id = 3,
            address = "",
            dateTime = "2021-09-06T17:30:45.123456",
            counter = Counter(
                id = 3,
                name = "Газ",
                type = CounterType.GAS,
                prev = 300.0,
                address = "",
                alreadyUsed = false
            ),
            value = 300.0
        ),
    )
}