package ru.psuti.apache1337.homeowners.data.requests.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.psuti.apache1337.homeowners.data.requests.db.dao.RequestDao
import ru.psuti.apache1337.homeowners.data.requests.db.entity.RequestEntity

@Database(
    entities = [RequestEntity::class],
    version = 1
)
@TypeConverters(DateConverter::class)
abstract class RequestsDatabase : RoomDatabase(){
    abstract fun requestDao(): RequestDao
}