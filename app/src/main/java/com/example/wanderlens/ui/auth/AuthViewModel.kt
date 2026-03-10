package com.example.wanderlens.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wanderlens.repository.AuthRepository
import com.example.wanderlens.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val authRepository = AuthRepository()

    private val _authState = MutableStateFlow<Resource<Boolean>?>(null)
    val authState: StateFlow<Resource<Boolean>?> = _authState.asStateFlow()

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            authRepository.loginWithEmail(email, pass).collect {
                _authState.value = it
            }
        }
    }

    fun register(name: String, email: String, pass: String) {
        viewModelScope.launch {
            authRepository.registerWithEmail(name, email, pass).collect {
                _authState.value = it
            }
        }
    }
    
    fun clearState() {
        _authState.value = null
    }
}
