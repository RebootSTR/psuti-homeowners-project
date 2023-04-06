package ru.psuti.apache1337.homeowners.data.counters.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import ru.psuti.apache1337.homeowners.data.counters.db.enitites.*
import ru.psuti.apache1337.homeowners.data.entities.User

@Dao
interface CountersDao {
    @Insert(onConflict = REPLACE)
    suspend fun updateCounters(counters: List<DbCounter>)

    @Query("SELECT * from counters")
    suspend fun getAllCounters(): List<DbCounter>

    @Insert(onConflict = REPLACE)
    suspend fun updateHistory(items: List<DbCounterHistoryItem>)

    @Query("SELECT * FROM history")
    suspend fun getAllHistory(): List<DbCounterHistoryItem>

    @Insert(onConflict = REPLACE)
    suspend fun cacheUnsentEntries(items: List<PendingCounterEntry>)

    @Query("DELETE FROM pendingEntries")
    suspend fun clearPendingEntries()

    @Query("SELECT * FROM pendingEntries")
    suspend fun getPendingEntries(): List<PendingCounterEntry>

    @Query("DELETE FROM counters")
    suspend fun clearCounters()

    @Query("SELECT * from user")
    suspend fun getUser(): User

    @Query("DELETE FROM history")
    suspend fun clearHistory()

    @Insert(onConflict = REPLACE)
    suspend fun setDemoCounters(items: List<DemoCounter>)

    @Insert(onConflict = REPLACE)
    suspend fun setDemoHistory(items: List<DemoHistoryItem>)

    @Query("SELECT * FROM countersDemo")
    suspend fun getDemoCounters(): List<DemoCounter>

    @Query("SELECT * FROM historyDemo")
    suspend fun getDemoHistory(): List<DemoHistoryItem>
}