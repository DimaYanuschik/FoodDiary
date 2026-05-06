// MainActivity.kt
package com.example.fooddiary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fooddiary.presentation.screens.auth.AuthViewModel
import com.example.fooddiary.presentation.screens.auth.LoginScreen
import com.example.fooddiary.presentation.screens.auth.RegisterScreen
import com.example.fooddiary.ui.screens.main.HomeScreen
import com.example.fooddiary.ui.screens.profile.CalorieGoalScreen
import com.example.fooddiary.ui.screens.profile.UserProfileScreen
import com.example.fooddiary.ui.theme.FoodDiaryTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FoodDiaryTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainNavigation()
                }
            }
        }
    }
}

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.authState.collectAsState()

    // Определяем стартовый экран на основе состояния авторизации
    val startDestination = remember(authState) {
        when {
            authState == null -> "login"
            authState?.isEmailVerified == true -> "home"
            else -> "profile_setup"
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Login Screen
        composable("login") {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate("register")
                },
                onNavigateToProfileSetup = {
                    navController.navigate("profile_setup") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                viewModel = authViewModel
            )
        }

        // Register Screen
        composable("register") {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onNavigateToProfileSetup = {
                    navController.navigate("profile_setup") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                viewModel = authViewModel
            )
        }

        // Profile Setup Screen
        composable("profile_setup") {
            UserProfileScreen(
                onComplete = {
                    navController.navigate("home") {
                        popUpTo("profile_setup") { inclusive = true }
                    }
                },
                onNavigateBack = {
                    authViewModel.signOut()
                    navController.navigate("login") {
                        popUpTo("profile_setup") { inclusive = true }
                    }
                }
            )
        }

        // Home Screen
        composable("home") {
            HomeScreen(
                onLogout = {
                    authViewModel.signOut()
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }

        // Calorie Goals Screen
        composable("calorie_goals") {
            CalorieGoalScreen(
                onComplete = {
                    navController.popBackStack()
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}