package com.app.homestyle.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.app.homestyle.model.UserAr
import com.app.homestyle.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    val allUsers: LiveData<List<UserAr>> = repository.allUsers

    fun insert(user: UserAr) = viewModelScope.launch {
        repository.insert(user)
    }

    fun delete(user: UserAr) = viewModelScope.launch {
        repository.delete(user)
    }

    fun getUserById(id: String): LiveData<UserAr?> {
        val user = MutableLiveData<UserAr?>()
        viewModelScope.launch {
            user.value = repository.getUserById(id)
        }
        return user
    }
}

class UserViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}