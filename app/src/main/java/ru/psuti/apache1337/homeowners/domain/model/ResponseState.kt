package ru.psuti.apache1337.homeowners.domain.model

sealed class ResponseState(
    val message: String? = null
) {
    object Loading : ResponseState()
    object Success : ResponseState()

    //    sealed class Failure : ResponseState() {
//        object NotFound : Failure()
//        object UnknownError : Failure()
//    }
    class Failure(private val error: String) : ResponseState(error)
}