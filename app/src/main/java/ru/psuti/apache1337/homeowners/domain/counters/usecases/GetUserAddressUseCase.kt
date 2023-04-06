package ru.psuti.apache1337.homeowners.domain.counters.usecases

import ru.psuti.apache1337.homeowners.domain.counters.CountersRepository
import javax.inject.Inject

class GetUserAddressUseCase @Inject constructor(
    private val repo: CountersRepository
) {
    suspend fun execute(): String {
        return repo.getAddress()
    }
}