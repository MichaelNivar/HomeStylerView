package com.app.homestylerview.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.homestylerview.repository.UserRepository

class UserArViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserArViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserArViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}