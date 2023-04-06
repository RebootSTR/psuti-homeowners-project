package ru.psuti.apache1337.homeowners.domain.counters.usecases

import ru.psuti.apache1337.homeowners.domain.counters.CountersRepository
import ru.psuti.apache1337.homeowners.domain.counters.model.CounterEntry
import javax.inject.Inject

class CacheUnsentEntriesUseCase @Inject constructor(
    private val repo: CountersRepository
) {
    suspend fun execute(list: List<CounterEntry>) {
        repo.cacheUnsentEntries(list)
    }
}