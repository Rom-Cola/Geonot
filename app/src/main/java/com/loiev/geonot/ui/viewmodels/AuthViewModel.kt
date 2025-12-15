package com.loiev.geonot.ui.viewmodels

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.loiev.geonot.data.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AuthRepository(application)

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        val user = repository.getCurrentUser()
        _authState.value = if (user != null) AuthState.Authenticated else AuthState.Unauthenticated
    }

    fun getGoogleSignInIntent(): Intent {
        return repository.getGoogleSignInIntent()
    }

    fun signInWithGoogle(intent: Intent) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                repository.firebaseSignInWithGoogle(intent)
                _authState.value = AuthState.Authenticated
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            repository.signOut()
            _authState.value = AuthState.Unauthenticated
        }
    }
}