package ru.psuti.apache1337.homeowners.domain.counters.model

enum class CounterType(val value: String) {
    NONE("-"), GAS("Газ"), WATER_COLD("Холодная вода"), WATER_HOT("Горячая вода"), ELECTRICITY("Электричество")
}