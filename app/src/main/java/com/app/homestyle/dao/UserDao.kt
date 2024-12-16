package com.app.homestyle.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.homestyle.model.UserAr

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserAr)

    @Delete
    suspend fun delete(user: UserAr)

    @Query("SELECT * FROM user_table WHERE email = :id")
    suspend fun getUserById(id: String): UserAr?

    @Query("SELECT * FROM user_table")
    fun getAllUsers(): LiveData<List<UserAr>>
}