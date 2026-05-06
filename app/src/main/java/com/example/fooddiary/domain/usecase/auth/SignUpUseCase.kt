package com.example.fooddiary.domain.usecase.auth

import com.example.fooddiary.domain.model.auth.User
import com.example.fooddiary.domain.repository.auth.IAuthRepository
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val authRepository: IAuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        confirmPassword: String
    ): Result<User> {

        // Валидация email
        if (!isValidEmail(email)) {
            return Result.failure(IllegalArgumentException("Неверный формат email"))
        }

        // Валидация пароля
        if (!isValidPassword(password)) {
            return Result.failure(IllegalArgumentException("Пароль должен содержать минимум 6 символов"))
        }

        if (password != confirmPassword) {
            return Result.failure(IllegalArgumentException("Пароли не совпадают"))
        }

        return authRepository.signUp(email, password)
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }
}