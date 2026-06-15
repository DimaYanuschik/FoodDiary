// presentation/navigation/AuthNavGraph.kt
package com.example.fooddiary.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.fooddiary.presentation.screens.auth.AuthViewModel
import com.example.fooddiary.presentation.screens.auth.LoginScreen
import com.example.fooddiary.presentation.screens.auth.RegisterScreen
import kotlinx.coroutines.flow.collectLatest

sealed class AuthDestination(val route: String) {
    object Login : AuthDestination("login")
    object Register : AuthDestination("register")
}

@Composable
fun AuthNavGraph(
    navController: NavHostController,
    onNavigateToProfileSetup: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    // Следим за состоянием авторизации через ViewModel
    LaunchedEffect(Unit) {
        viewModel.authState.collectLatest { user ->
            if (user != null) {
                if (user.isEmailVerified) {
                    onNavigateToHome()
                } else {
                    onNavigateToProfileSetup()
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = AuthDestination.Login.route
    ) {
//        composable(AuthDestination.Login.route) {
//            LoginScreen(
//                onNavigateToRegister = {
//                    navController.navigate(AuthDestination.Register.route)
//                },
//                onNavigateToProfileSetup = onNavigateToProfileSetup,
//                onNavigateToHome = onNavigateToHome,
//                viewModel = viewModel
//            )
//        }

        composable(AuthDestination.Register.route) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onNavigateToProfileSetup = onNavigateToProfileSetup,
                viewModel = viewModel
            )
        }
    }
}