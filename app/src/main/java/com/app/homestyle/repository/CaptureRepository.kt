package com.app.homestyle.repository

import com.app.homestyle.dao.CaptureDao
import com.app.homestyle.model.Capture

class CaptureRepository(private val captureDao: CaptureDao) {

    suspend fun insertCapture(capture: Capture) {
        captureDao.insertCapture(capture)
    }

    suspend fun getCapturesByUser(userEmail: String): List<Capture> {
        return captureDao.getCapturesByUser(userEmail)
    }

    suspend fun deleteCapturesByUser(userEmail: String) {
        captureDao.deleteCapturesByUser(userEmail)
    }
}
