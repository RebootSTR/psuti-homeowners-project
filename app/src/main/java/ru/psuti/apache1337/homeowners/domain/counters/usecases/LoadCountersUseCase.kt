package ru.psuti.apache1337.homeowners.domain.counters.usecases

import ru.psuti.apache1337.homeowners.domain.counters.CountersRepository
import ru.psuti.apache1337.homeowners.domain.counters.model.Counter
import javax.inject.Inject

class LoadCountersUseCase @Inject constructor(
    private val repo: CountersRepository
) {
    suspend fun execute(): List<Counter> {
        try {
            repo.updateCounters()
        } catch (e: Exception) {
            throw e
        }
        return repo.getCountersList()
    }
}