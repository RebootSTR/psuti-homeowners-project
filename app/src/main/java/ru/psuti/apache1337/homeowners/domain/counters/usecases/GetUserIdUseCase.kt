package ru.psuti.apache1337.homeowners.domain.counters.usecases

import ru.psuti.apache1337.homeowners.domain.counters.CountersRepository
import javax.inject.Inject

class GetUserIdUseCase @Inject constructor(
    private val repo: CountersRepository
) {
    fun execute(): Int{
        return repo.getUserId()
    }
}