package ru.psuti.apache1337.homeowners.domain.counters.usecases

import ru.psuti.apache1337.homeowners.domain.counters.CountersRepository
import ru.psuti.apache1337.homeowners.domain.counters.model.CounterHistoryEntry
import javax.inject.Inject

class LoadCountersHistoryUseCase @Inject constructor(
    private val repo: CountersRepository
) {
    suspend fun execute(): List<CounterHistoryEntry> {
        try {
            repo.updateHistory()

        } catch (e: Exception) {
            throw e
        }
        return repo.getCountersHistory().reversed()
    }
}