package ru.psuti.apache1337.homeowners.domain.counters.usecases

import ru.psuti.apache1337.homeowners.domain.counters.CountersRepository
import ru.psuti.apache1337.homeowners.domain.counters.model.CounterDate
import ru.psuti.apache1337.homeowners.domain.counters.model.CounterFilter
import ru.psuti.apache1337.homeowners.domain.counters.model.CounterHistoryEntry
import ru.psuti.apache1337.homeowners.domain.counters.model.CounterType
import java.time.ZonedDateTime
import javax.inject.Inject

class FilterHistoryUseCase @Inject constructor(
    private val repo: CountersRepository
) {
    suspend fun execute(filter: CounterFilter, demo: Boolean): List<CounterHistoryEntry> {
        val res: List<CounterHistoryEntry> = if (!demo) {
            repo.getCountersHistory()
        } else {
            repo.getDemoHistory()
        }

        var sorted = res

        when (filter.filterDate) {
            CounterDate.ALL -> sorted = res
            CounterDate.LAST_MONTH -> sorted = sorted.filter {
                val now = ZonedDateTime.now()
                val lastMonth = now.minusMonths(2)
                return@filter it.date.isAfter(lastMonth)
            }
            CounterDate.LAST_YEAR -> sorted = sorted.filter {
                val now = ZonedDateTime.now()
                val lastYear = now.minusYears(1)
                return@filter it.date.isAfter(lastYear)
            }
            CounterDate.LAST_QUARTER -> sorted = sorted.filter {
                val now = ZonedDateTime.now()
                val lastQuarter = now.minusMonths(3)
                return@filter it.date.isAfter(lastQuarter)
            }
        }

        when (filter.filterType) {
            CounterType.NONE -> {
            }
            CounterType.GAS -> sorted = sorted.filter {
                it.counter.type == CounterType.GAS
            }
            CounterType.WATER_HOT -> sorted = sorted.filter {
                it.counter.type == CounterType.WATER_HOT
            }
            CounterType.WATER_COLD -> sorted = sorted.filter {
                it.counter.type == CounterType.WATER_COLD
            }
            CounterType.ELECTRICITY -> sorted = sorted.filter {
                it.counter.type == CounterType.ELECTRICITY
            }
        }

        return sorted.reversed()
    }
}