package com.example.fooddiary.domain.usecase.auth

import com.example.fooddiary.domain.model.auth.User
import com.example.fooddiary.domain.repository.auth.IAuthRepository

class SignInWithGoogleUseCase(private val repository: IAuthRepository) {
    suspend operator fun invoke(idToken: String): Result<User> {
        return repository.signInWithGoogle(idToken)
    }
}