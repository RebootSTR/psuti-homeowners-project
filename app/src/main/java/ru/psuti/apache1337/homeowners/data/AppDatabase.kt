package ru.psuti.apache1337.homeowners.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.psuti.apache1337.homeowners.data.auth.db.AuthDao
import ru.psuti.apache1337.homeowners.data.counters.db.CountersDao
import ru.psuti.apache1337.homeowners.data.counters.db.enitites.*
import ru.psuti.apache1337.homeowners.data.entities.User
import ru.psuti.apache1337.homeowners.data.profile.db.UserDao

@Database(
    entities = [User::class, DbCounter::class, DbCounterHistoryItem::class, PendingCounterEntry::class, DemoCounter::class, DemoHistoryItem::class],
    version = 2
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun authDao(): AuthDao
    abstract fun countersDao(): CountersDao
    abstract fun userDao(): UserDao
}