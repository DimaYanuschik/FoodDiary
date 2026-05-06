package com.example.fooddiary.domain.usecase.auth

import com.example.fooddiary.domain.repository.auth.IAuthRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val authRepository: IAuthRepository
){
    suspend operator fun invoke(): Result<Unit> {
        return try {
            authRepository.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}