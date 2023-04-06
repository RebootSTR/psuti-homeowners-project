package ru.psuti.apache1337.homeowners.domain.counters.usecases

import ru.psuti.apache1337.homeowners.domain.counters.CountersRepository
import ru.psuti.apache1337.homeowners.domain.counters.model.CounterEntry
import javax.inject.Inject

class SendCounterEntriesUseCase @Inject constructor(
    private val repo: CountersRepository
) {
    suspend fun execute(entries: List<CounterEntry>) {
        try {
            repo.sendCounterEntries(entries)
        } catch (e: Exception) {
            throw e
        }
    }
}