package ru.psuti.apache1337.homeowners.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey val id: Int,
    val phone: String,
    val email: String,
    val firstName: String,
    val secondName: String,
    val patronymic: String,
    val city: String,
    val street: String,
    val house: Int,
    val building: String?,
    val apartment: Int,
)