package com.app.homestyle.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.app.homestyle.model.Capture

@Dao
interface CaptureDao {
    @Insert
    suspend fun insertCapture(capture: Capture)

    @Query("SELECT * FROM Capture WHERE userEmail = :userEmail")
    suspend fun getCapturesByUser(userEmail: String): List<Capture>

    @Query("DELETE FROM Capture WHERE userEmail = :userEmail")
    suspend fun deleteCapturesByUser(userEmail: String)
}
