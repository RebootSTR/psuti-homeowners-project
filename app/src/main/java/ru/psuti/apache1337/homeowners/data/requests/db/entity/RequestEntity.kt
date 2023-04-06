package ru.psuti.apache1337.homeowners.data.requests.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "requests")
data class RequestEntity(
    @PrimaryKey var id: Int,
    var theme: String,
    var date: String,
    var addressId: Int,
    var text: String,
    var statusId: Int,
    var userId: Int,
    var file: String?,

    var lastRefresh: Date
)