package com.example.wanderlens.repository

import com.google.firebase.auth.FirebaseAuth
import com.example.wanderlens.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun loginWithEmail(email: String, pass: String): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading)
        try {
            auth.signInWithEmailAndPassword(email, pass).await()
            emit(Resource.Success(true))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Login failed"))
        }
    }

    fun registerWithEmail(name: String, email: String, pass: String): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading)
        try {
            val result = auth.createUserWithEmailAndPassword(email, pass).await()
            // Optional: Update profile with name
            val profileUpdates = com.google.firebase.auth.userProfileChangeRequest {
                displayName = name
            }
            result.user?.updateProfile(profileUpdates)?.await()
            emit(Resource.Success(true))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Registration failed"))
        }
    }

    fun logout() {
        auth.signOut()
    }
}
