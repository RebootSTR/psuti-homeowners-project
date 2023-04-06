package ru.psuti.apache1337.homeowners.data.requests.remote.dto

data class RequestDTO(
    val id: Int?,
    val type: Int?,
    val title: String,
    val date: String?,
    val address: Int?,
    val comment: String,
    val status: Int,
    val client: Int,
    val fileName: String?
)