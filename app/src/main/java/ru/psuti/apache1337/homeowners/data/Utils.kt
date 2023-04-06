package ru.psuti.apache1337.homeowners.data

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


fun String.toPhone() : String {
    return this.replace("[() +]+".toRegex(), "")
}

fun File.toMultipart(fileName: String): MultipartBody.Part {
    return MultipartBody.Part.createFormData(
        "file",
        fileName,
        this.asRequestBody("*/*".toMediaType()))
}