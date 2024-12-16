package com.app.homestyle.repository

import androidx.lifecycle.LiveData
import com.app.homestyle.dao.UserDao
import com.app.homestyle.model.UserAr

class UserRepository(private val userDao: UserDao) {

    val allUsers: LiveData<List<UserAr>> = userDao.getAllUsers()

    suspend fun insert(user: UserAr) {
        userDao.insert(user)
    }

    suspend fun delete(user: UserAr) {
        userDao.delete(user)
    }

    suspend fun getUserById(id: String): UserAr? {
        return userDao.getUserById(id)
    }
}