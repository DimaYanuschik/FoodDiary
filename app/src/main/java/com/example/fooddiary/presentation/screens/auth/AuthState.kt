//// presentation/screens/auth/AuthState.kt
//package com.example.fooddiary.presentation.screens.auth
//
//import com.example.fooddiary.domain.model.auth.User
//
//sealed class AuthState {
//    object Idle : AuthState()
//    object Loading : AuthState()
//    data class Success(val user: User) : AuthState()
//    data class Error(val message: String) : AuthState()
//}
//
//sealed class NavigationEvent {
//    object NavigateToProfileSetup : NavigationEvent()
//    object NavigateToHome : NavigationEvent()
//    object NavigateToLogin : NavigationEvent()
//    object NavigateToRegister : NavigationEvent()
//}
//
//data class LoginUiState(
//    val email: String = "",
//    val password: String = "",
//    val isLoading: Boolean = false,
//    val errorMessage: String? = null
//)
//
//data class RegisterUiState(
//    val email: String = "",
//    val password: String = "",
//    val confirmPassword: String = "",
//    val isLoading: Boolean = false,
//    val errorMessage: String? = null
//)