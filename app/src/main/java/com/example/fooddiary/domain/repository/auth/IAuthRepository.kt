package com.example.fooddiary.domain.repository.auth

import com.example.fooddiary.domain.model.auth.User
import kotlinx.coroutines.flow.Flow

interface IAuthRepository {
    fun authStateFlow(): Flow<User?>

    suspend fun signIn(email: String, password: String): Result<User>

    suspend fun  signUp(email: String, password: String): Result<User>

    suspend fun signOut(): Result<Unit>

    fun isUserLoggedIn(): Boolean

    fun getCurrentUser(): User?
}