package ru.psuti.apache1337.homeowners.data.profile.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.psuti.apache1337.homeowners.data.entities.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(user: User)

    @Query("SELECT * FROM user WHERE phone = :number")
    fun loadByNumber(number: String): LiveData<User>
}