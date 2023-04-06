package ru.psuti.apache1337.homeowners.domain.counters

import ru.psuti.apache1337.homeowners.domain.counters.model.Counter
import ru.psuti.apache1337.homeowners.domain.counters.model.CounterEntry
import ru.psuti.apache1337.homeowners.domain.counters.model.CounterHistoryEntry

interface CountersRepository {
    suspend fun updateCounters()
    suspend fun getCountersList(): List<Counter>
    suspend fun updateHistory()
    suspend fun getCountersHistory(): List<CounterHistoryEntry>
    suspend fun sendCounterEntries(values: List<CounterEntry>)
    suspend fun cacheUnsentEntries(values: List<CounterEntry>)
    suspend fun clearCachedEntries()
    suspend fun clearHistory()
    suspend fun sendCachedEntries()
    suspend fun getAddress(): String
    suspend fun getDemoCounters(): List<Counter>
    suspend fun getDemoHistory(): List<CounterHistoryEntry>
    suspend fun setDemoData()
    fun getUserId(): Int
}