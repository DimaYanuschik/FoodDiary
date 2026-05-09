// presentation/screens/auth/AuthViewModel.kt
package com.example.fooddiary.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fooddiary.domain.model.auth.User
import com.example.fooddiary.domain.usecase.auth.CheckAuthStatusUseCase
import com.example.fooddiary.domain.usecase.auth.SignInUseCase
import com.example.fooddiary.domain.usecase.auth.SignOutUseCase
import com.example.fooddiary.domain.usecase.auth.SignUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase,
    private val signUpUseCase: SignUpUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val checkAuthStatusUseCase: CheckAuthStatusUseCase
) : ViewModel() {

    // Состояние авторизации
    private val _authState = MutableStateFlow<User?>(null)
    val authState: StateFlow<User?> = _authState.asStateFlow()

    // UI State для логина
    private val _loginUiState = MutableStateFlow(LoginUiState())
    val loginUiState: StateFlow<LoginUiState> = _loginUiState.asStateFlow()

    // UI State для регистрации
    private val _registerUiState = MutableStateFlow(RegisterUiState())
    val registerUiState: StateFlow<RegisterUiState> = _registerUiState.asStateFlow()

    init {
        // Следим за статусом авторизации
        checkAuthStatusUseCase()
            .onEach { user ->
                _authState.value = user
            }
            .launchIn(viewModelScope)
    }

    fun updateLoginEmail(email: String) {
        _loginUiState.value = _loginUiState.value.copy(email = email)
    }

    fun updateLoginPassword(password: String) {
        _loginUiState.value = _loginUiState.value.copy(password = password)
    }

    fun updateRegisterEmail(email: String) {
        _registerUiState.value = _registerUiState.value.copy(email = email)
    }

    fun updateRegisterPassword(password: String) {
        _registerUiState.value = _registerUiState.value.copy(password = password)
    }

    fun updateRegisterConfirmPassword(confirmPassword: String) {
        _registerUiState.value = _registerUiState.value.copy(confirmPassword = confirmPassword)
    }

    fun signIn() {
        val currentState = _loginUiState.value
        val email = currentState.email
        val password = currentState.password

        viewModelScope.launch {
            _loginUiState.value = currentState.copy(isLoading = true, errorMessage = null)

            val result = signInUseCase(email, password)

            result.fold(
                onSuccess = { user ->
                    _loginUiState.value = _loginUiState.value.copy(isLoading = false)
                    _authState.value = user
                },
                onFailure = { error ->
                    _loginUiState.value = _loginUiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Ошибка входа"
                    )
                }
            )
        }
    }

    fun signUp() {
        val currentState = _registerUiState.value
        val email = currentState.email
        val password = currentState.password
        val confirmPassword = currentState.confirmPassword

        viewModelScope.launch {
            _registerUiState.value = currentState.copy(isLoading = true, errorMessage = null)

            val result = signUpUseCase(email, password, confirmPassword)

            result.fold(
                onSuccess = { user ->
                    _registerUiState.value = _registerUiState.value.copy(isLoading = false)
                    _authState.value = user
                },
                onFailure = { error ->
                    _registerUiState.value = _registerUiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Ошибка регистрации"
                    )
                }
            )
        }
    }

    fun signOut() {
        viewModelScope.launch {
            signOutUseCase()
            _authState.value = null
        }
    }

    fun clearForms() {
        _loginUiState.value = LoginUiState()
        _registerUiState.value = RegisterUiState()
    }

    override fun onCleared() {
        super.onCleared()
        clearForms()
    }
}

// State data classes
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed class NavigationEvent {
    object NavigateToProfileSetup : NavigationEvent()
    object NavigateToHome : NavigationEvent()
    object NavigateToLogin : NavigationEvent()
    object NavigateToRegister : NavigationEvent()
}