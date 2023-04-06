package ru.psuti.apache1337.homeowners.domain.counters.usecases

import ru.psuti.apache1337.homeowners.domain.counters.CountersRepository
import javax.inject.Inject

class SendCachedEntriesUseCase @Inject constructor(
    private val repo: CountersRepository
) {
    suspend fun execute() {
        try {
            repo.sendCachedEntries()
        } catch (e: Exception) {
            throw e
        }
    }
}