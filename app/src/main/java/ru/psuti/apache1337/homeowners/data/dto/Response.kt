package ru.psuti.apache1337.homeowners.data.dto

sealed class Response {

    data class Success<T>(val payload: T) : Response()
    data class Failure(val error: String) : Response()
}