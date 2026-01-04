package com.example.fooddiary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.fooddiary.data.auth.AuthRepository
//import com.example.fooddiary.ui.navigation.FoodDiaryNavHost
import com.example.fooddiary.ui.screens.auth.LoginScreen
import com.example.fooddiary.ui.screens.auth.RegisterScreen
import com.example.fooddiary.ui.screens.main.HomeScreen
import com.example.fooddiary.ui.screens.profile.CalorieGoalScreen
import com.example.fooddiary.ui.screens.profile.UserProfileScreen
import com.example.fooddiary.ui.theme.FoodDiaryTheme
import com.example.fooddiary.ui.viewmodels.AuthViewModel
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
                    MainApp()
                }
            }
        }
    }
}

@Composable
fun MainApp() {
    val authViewModel: AuthViewModel = hiltViewModel()
    var currentScreen by remember { mutableStateOf<Screens>(Screens.Login) }

    val isUserLoggedIn by remember(authViewModel.isUserLoggedIn) {
        derivedStateOf { authViewModel.isUserLoggedIn }
    }

    // Автоматически определяем стартовый экран
    LaunchedEffect(isUserLoggedIn) {
        currentScreen = if (isUserLoggedIn) Screens.Home else Screens.Login
    }


//    LaunchedEffect(authViewModel.isUserLoggedIn) {
//        currentScreen = if (authViewModel.isUserLoggedIn) Screens.Home else Screens.Login
//    }

    when (currentScreen) {
        Screens.Login -> LoginScreen(
            onLoginSuccess = { currentScreen = Screens.ProfileSetup },
            onNavigateToRegister = { currentScreen = Screens.Register }
        )

        Screens.Register -> RegisterScreen(
            onRegisterSuccess = { currentScreen = Screens.ProfileSetup },
            onNavigateToLogin = { currentScreen = Screens.Login }
        )

        Screens.ProfileSetup -> UserProfileScreen(
            onComplete = { currentScreen = Screens.Home },
            onNavigateBack = {
                authViewModel.signOut()
                currentScreen = Screens.Login
            }
        )

        Screens.Home -> HomeScreen(
            onLogout = {
                authViewModel.signOut()
                currentScreen = Screens.Login
            }
        )

        Screens.CalorieGoals -> CalorieGoalScreen(
            onComplete = { currentScreen = Screens.Home },
            onNavigateBack = { currentScreen = Screens.Home }
        )
    }
}


//@Composable
//fun FoodDiaryApp() {
////    val authRepository = AuthRepository()
//    val authViewModel: AuthViewModel = hiltViewModel() // Используем Hilt для внедрения
//
//    var currentScreen by remember { mutableStateOf<Screens>(Screens.Login) }
//
//    // Автоматически определяем стартовый экран
//    LaunchedEffect(Unit) {
//        currentScreen = if (authRepository.isLoggedIn) Screens.Home else Screens.Login
//    }
//
//    when (currentScreen) {
//        Screens.Login -> LoginScreen(
//            onLoginSuccess = { currentScreen = Screens.ProfileSetup },
//            onNavigateToRegister = { currentScreen = Screens.Register }
//        )
//
//        Screens.Register -> RegisterScreen(
//            onRegisterSuccess = { currentScreen = Screens.ProfileSetup },
//            onNavigateToLogin = { currentScreen = Screens.Login }
//        )
//
//        Screens.ProfileSetup -> UserProfileScreen(
//            onComplete = { currentScreen = Screens.Home },
//            onNavigateBack = {
//                authRepository.signOut()
//                currentScreen = Screens.Login
//            }
//        )
//
//        Screens.Home -> HomeScreen(
//            onLogout = {
//                authRepository.signOut()
//                currentScreen = Screens.Login
//            }
//        )
//
//        Screens.CalorieGoals -> CalorieGoalScreen(
//            onComplete = { currentScreen = Screens.Home },
//            onNavigateBack = { currentScreen = Screens.Home }
//        )
//    }
//}

sealed class Screens {
    object Login : Screens()
    object Register : Screens()
    object ProfileSetup : Screens()
    object Home : Screens()
    object CalorieGoals : Screens()
}