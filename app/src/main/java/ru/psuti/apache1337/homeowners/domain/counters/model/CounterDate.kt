package ru.psuti.apache1337.homeowners.domain.counters.model

enum class CounterDate(val value: String) {
    ALL("За все время"), LAST_MONTH("За предыдущий месяц"), LAST_YEAR("За последний год"), LAST_QUARTER("За последний квартал")
}