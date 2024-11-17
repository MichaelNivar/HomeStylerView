package com.app.homestylerview.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.homestylerview.model.UserAr
import com.app.homestylerview.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date

class UserArViewModel(private val repository: UserRepository): ViewModel() {
    // Estado para monitorear el registro del usuario
    private val _registrationState = MutableStateFlow<Result<Boolean>?>(null)
    val registrationState: StateFlow<Result<Boolean>?> = _registrationState

    fun registerUser(nombre: String, email: String, password: String) {
        viewModelScope.launch {
            val usuario = UserAr(
                nombre = nombre,
                email = email,
                password = password,
                fechaCreacion = Date()
            )
            val result = repository.registerUserWithAuthentication(usuario)
            _registrationState.value = result
        }
    }
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    // Estado para monitorear la autenticación
    private val _authState = MutableStateFlow<Result<Boolean>?>(null)
    val authState: StateFlow<Result<Boolean>?> = _authState

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            _authState.value = Result.success(true)
                        } else {
                            _authState.value = Result.failure(task.exception ?: Exception("Error desconocido"))
                        }
                    }
            } catch (e: Exception) {
                _authState.value = Result.failure(e)
            }
        }
    }
    // Estado para monitorear el logout
    private val _logoutState = MutableStateFlow<Result<Boolean>?>(null)
    val logoutState: StateFlow<Result<Boolean>?> = _logoutState

    fun logoutUser() {
        viewModelScope.launch {
            try {
                auth.signOut()
                _logoutState.value = Result.success(true)
            } catch (e: Exception) {
                _logoutState.value = Result.failure(e)
            }
        }
    }

    private val _user = MutableLiveData<UserAr?>()
    val user: LiveData<UserAr?> = _user

    fun fetchUserById(userId: String) {
        viewModelScope.launch {
            val result = repository.getUserById(userId)
            result.onSuccess { user ->
                _user.postValue(user)
            }.onFailure { exception ->
                _user.postValue(null) // Maneja el error según tu lógica
                Log.e("UserViewModel", "Error fetching user: ${exception.message}")
            }
        }
    }
}