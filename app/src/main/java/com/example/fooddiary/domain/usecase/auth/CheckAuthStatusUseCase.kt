package com.example.fooddiary.domain.usecase.auth

import com.example.fooddiary.domain.model.auth.User
import com.example.fooddiary.domain.repository.auth.IAuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CheckAuthStatusUseCase @Inject constructor(
    private val authRepository: IAuthRepository
) {
    operator fun invoke(): Flow<User?> {
        return authRepository.authStateFlow()
    }
}