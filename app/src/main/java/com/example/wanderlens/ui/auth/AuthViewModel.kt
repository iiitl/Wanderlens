package com.example.wanderlens.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    val passStr = MutableLiveData<Int>()
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

    fun onPass(pass: String) {
        if(pass.isEmpty()) {
            passStr.value = 0
            return
        }
        passStr.value = calcStr(pass)
    }

    private fun calcStr(pass: String): Int {
        var score = 0
        if(pass.length >= 8) score+=30
        if(pass.length >= 10) score+=20
        if(pass.any { it.isUpperCase() }) score+=15
        if(pass.any { it.isLowerCase() }) score+=15
        if(pass.any { it.isDigit() }) score+=20
        if(pass.any { !it.isLetterOrDigit() }) score+=10
        if(score >= 100) return 100
        else return score
    }
}
