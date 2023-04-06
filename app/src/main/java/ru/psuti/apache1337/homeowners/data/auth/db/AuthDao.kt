package ru.psuti.apache1337.homeowners.data.auth.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import ru.psuti.apache1337.homeowners.data.entities.User

@Dao
interface AuthDao {
    @Insert(onConflict = REPLACE)
    suspend fun save(user: User)
}