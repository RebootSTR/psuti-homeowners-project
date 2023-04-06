package ru.psuti.apache1337.homeowners.domain.counters.usecases

import ru.psuti.apache1337.homeowners.data.counters.CountersDemoData
import ru.psuti.apache1337.homeowners.domain.counters.CountersRepository
import ru.psuti.apache1337.homeowners.domain.counters.model.Counter
import javax.inject.Inject

class GetDemoCountersUseCase @Inject constructor(
    private val repo: CountersRepository
) {
    suspend fun execute(): List<Counter> {
        val res = repo.getDemoCounters()
        if (res.isEmpty()) {
            repo.setDemoData()
        }
        return repo.getDemoCounters()
    }
}