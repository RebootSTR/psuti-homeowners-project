package ru.psuti.apache1337.homeowners.domain.requests.usecases

import ru.psuti.apache1337.homeowners.data.dto.Response
import ru.psuti.apache1337.homeowners.data.toMultipart
import ru.psuti.apache1337.homeowners.domain.requests.db.repository.RequestRepository
import java.io.File
import javax.inject.Inject

class SendFileUseCase @Inject constructor(
    private val requestRepository: RequestRepository
) {

    suspend fun execute(file: File, fileName: String): Response {
        return requestRepository.sendFile(file.toMultipart(fileName))
    }
}