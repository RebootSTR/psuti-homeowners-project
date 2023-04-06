package ru.psuti.apache1337.homeowners.domain.counters.usecases

import ru.psuti.apache1337.homeowners.domain.counters.CountersRepository
import ru.psuti.apache1337.homeowners.domain.counters.model.CounterHistoryEntry
import javax.inject.Inject

class GetDemoHistoryUseCase @Inject constructor(
    private val repo: CountersRepository
) {
    suspend fun execute(): List<CounterHistoryEntry> {
        val res = repo.getDemoHistory()
        if (res.isEmpty()) {
            repo.setDemoData()
        }
        return repo.getDemoHistory()
    }
}