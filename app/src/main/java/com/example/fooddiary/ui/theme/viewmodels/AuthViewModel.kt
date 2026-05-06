package com.example.fooddiary.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fooddiary.data_old.auth.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
//class AuthViewModel : ViewModel() {
//    private val authRepository = AuthRepository()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    val isUserLoggedIn: Boolean
        get() = authRepository.isLoggedIn

    fun signIn(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val success = authRepository.signInWithEmail(email, password)

            if (success) {
                onSuccess()
            } else {
                _errorMessage.value = "Ошибка входа. Проверьте email и пароль."
            }

            _isLoading.value = false
        }
    }

    fun signUp(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val success = authRepository.signUpWithEmail(email, password)

            if (success) {
                onSuccess()
            } else {
                _errorMessage.value = "Ошибка регистрации. Email может быть уже занят."
            }

            _isLoading.value = false
        }
    }

//    fun isUserLoggedIn(): Boolean {
//        return authRepository.isLoggedIn
//    }

    fun signOut() {
        authRepository.signOut()
    }
}