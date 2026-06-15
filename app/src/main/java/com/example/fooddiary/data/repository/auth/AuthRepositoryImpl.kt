package com.example.fooddiary.data.repository.auth

import com.example.fooddiary.domain.model.auth.User
import com.example.fooddiary.domain.repository.auth.IAuthRepository
import com.example.fooddiary.data.datasource.remote.auth.FirebaseAuthDataSource
import com.example.fooddiary.data.model.auth.FirebaseUserDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val dataSource: FirebaseAuthDataSource
): IAuthRepository {
    override fun authStateFlow(): Flow<User?> {
        return dataSource.getAuthStateFlow().map { dto ->
            dto?.let { mapToDomain(it) }
        }
    }

    override suspend fun signIn(email: String, password: String): Result<User> {
        return try {
            val userDto = dataSource.signInWithEmail(email, password)
            userDto?.let {
                Result.success(mapToDomain(it))
            } ?: Result.failure(Exception("Пользователь не найден"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signUp(email: String, password: String): Result<User> {
        return try {
            val userDto = dataSource.signUpWithEmail(email, password)
            userDto?.let {
                Result.success(mapToDomain(it))
            } ?: Result.failure(Exception("Не удалось создать аккаунт"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signInWithGoogle(idToken: String): Result<User> {
        return try {
            val userDto = dataSource.signInWithGoogle(idToken)
            Result.success(mapToDomain(userDto))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return try {
            dataSource.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun isUserLoggedIn(): Boolean {
        return dataSource.isUserLoggedIn()
    }

    override fun getCurrentUser(): User? {
        val userDto = dataSource.getCurrentUser()
        return userDto?.let { mapToDomain(it) }
    }

    // Mapper: DTO -> Domain Model
    private fun mapToDomain(dto: FirebaseUserDto): User {
        return User(
            uid = dto.uid,
            email = dto.email ?: "",
            isEmailVerified = dto.isEmailVerified
        )
    }
}