package com.example.fooddiary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.fooddiary.data.auth.AuthRepository
import com.example.fooddiary.ui.screens.auth.LoginScreen
import com.example.fooddiary.ui.screens.auth.RegisterScreen
import com.example.fooddiary.ui.screens.main.HomeScreen
import com.example.fooddiary.ui.theme.FoodDiaryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FoodDiaryTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FoodDiaryApp()
                }
            }
        }
    }
}

@Composable
fun FoodDiaryApp() {
    val authRepository = AuthRepository()
    var isLoggedIn by remember { mutableStateOf(authRepository.isLoggedIn) }
    var showRegisterScreen by remember { mutableStateOf(false) }

    // Проверяем состояние аутентификации
    LaunchedEffect(Unit) {
        isLoggedIn = authRepository.isLoggedIn
    }

    when {
        isLoggedIn -> {
            HomeScreen(
                onLogout = {
                    authRepository.signOut()
                    isLoggedIn = false
                }
            )
        }
        showRegisterScreen -> {
            RegisterScreen(
                onRegisterSuccess = {
                    isLoggedIn = true
                    showRegisterScreen = false
                },
                onNavigateToLogin = {
                    showRegisterScreen = false
                }
            )
        }
        else -> {
            LoginScreen(
                onLoginSuccess = { isLoggedIn = true },
                onNavigateToRegister = { showRegisterScreen = true }
            )
        }
    }
}