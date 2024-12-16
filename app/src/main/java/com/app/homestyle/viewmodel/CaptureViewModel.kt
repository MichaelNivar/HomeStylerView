package com.app.homestyle.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.app.homestyle.model.Capture
import com.app.homestyle.repository.CaptureRepository
import kotlinx.coroutines.launch

class CaptureViewModel(private val repository: CaptureRepository) : ViewModel() {

    private val _captures = MutableLiveData<List<Capture>>()
    val captures: LiveData<List<Capture>> get() = _captures

    fun fetchCapturesForUser(userEmail: String) {
        viewModelScope.launch {
            val captures = repository.getCapturesByUser(userEmail)
            _captures.postValue(captures)
        }
    }

    fun saveCapture(capture: Capture) {
        viewModelScope.launch {
            repository.insertCapture(capture)
        }
    }
}

class CaptureViewModelFactory(private val repository: CaptureRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CaptureViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CaptureViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
