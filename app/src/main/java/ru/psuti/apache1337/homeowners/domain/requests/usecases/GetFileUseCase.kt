package ru.psuti.apache1337.homeowners.domain.requests.usecases

import ru.psuti.apache1337.homeowners.data.dto.Response
import ru.psuti.apache1337.homeowners.domain.requests.db.repository.RequestRepository
import javax.inject.Inject

class GetFileUseCase @Inject constructor(
    private val requestRepository: RequestRepository
){

    suspend fun execute(fileName: String): Response {
        return requestRepository.getFile(fileName)
    }
}