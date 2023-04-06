package ru.psuti.apache1337.homeowners.data.requests.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import ru.psuti.apache1337.homeowners.data.requests.db.entity.RequestEntity
import java.util.*

@Dao
interface RequestDao {

    @Insert(onConflict = REPLACE)
    suspend fun save(requestEntity: RequestEntity)

    @Insert(onConflict = REPLACE)
    @JvmSuppressWildcards
    suspend fun saveAll(requestEntities: List<RequestEntity>)

    @Query("SELECT * FROM requests WHERE userId = :userId")
    fun loadByUserId(userId: Int): LiveData<List<RequestEntity>>

    @Query("SELECT * FROM requests WHERE userId = :userId AND lastRefresh > :lastRefreshMax LIMIT 1")
    fun hasRequests(userId: Int, lastRefreshMax: Date): Boolean
}