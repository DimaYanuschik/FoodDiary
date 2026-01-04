//package com.example.fooddiary.ui.navigation
//
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//import com.example.fooddiary.data.auth.AuthRepository
//import com.example.fooddiary.ui.screens.auth.LoginScreen
//import com.example.fooddiary.ui.screens.auth.RegisterScreen
//import com.example.fooddiary.ui.screens.main.HomeScreen
//import com.example.fooddiary.ui.screens.profile.UserProfileScreen
//
//@Composable
//fun FoodDiaryNavHost(
//    navController: NavHostController = rememberNavController()
//) {
//    val authRepository = AuthRepository()
//    var isLoggedIn by remember { mutableStateOf(authRepository.isLoggedIn) }
//
//    // Проверяем аутентификацию при запуске
//    LaunchedEffect(Unit) {
//        isLoggedIn = authRepository.isLoggedIn
//    }
//
//    // Стартовый экран в зависимости от авторизации
//    val startDestination = if (isLoggedIn) {
//        Screens.Home.route
//    } else {
//        Screens.Login.route
//    }
//
//    NavHost(
//        navController = navController,
//        startDestination = startDestination
//    ) {
//        // Auth screens
//        composable(Screens.Login.route) {
//            LoginScreen(
//                onLoginSuccess = {
//                    isLoggedIn = true
//                    navController.navigate(Screens.Home.route) {
//                        popUpTo(Screens.Login.route) { inclusive = true }
//                    }
//                },
//                onNavigateToRegister = {
//                    navController.navigate(Screens.Register.route)
//                }
//            )
//        }
//
//        composable(Screens.Register.route) {
//            RegisterScreen(
//                onRegisterSuccess = {
//                    isLoggedIn = true
//                    navController.navigate(Screens.Home.route) {
//                        popUpTo(Screens.Login.route) { inclusive = true }
//                    }
//                },
//                onNavigateToLogin = {
//                    navController.popBackStack()
//                }
//            )
//        }
//
//        // Main screens
//        composable(Screens.Home.route) {
//            HomeScreen(
//                onLogout = {
//                    authRepository.signOut()
//                    isLoggedIn = false
//                    navController.navigate(Screens.Login.route) {
//                        popUpTo(Screens.Home.route) { inclusive = true }
//                    }
//                },
//                onOpenProfile = {
//                    navController.navigate(Screens.UserProfile.route)
//                }
//            )
//        }
//
//        composable(Screens.UserProfile.route) {
//            UserProfileScreen(
//                onComplete = {
//                    navController.popBackStack()
//                },
//                onNavigateBack = {
//                    navController.popBackStack()
//                }
//            )
//        }
//    }
//}
//
//// Определение экранов
//sealed class Screens(val route: String) {
//    object Login : Screens("login")
//    object Register : Screens("register")
//    object Home : Screens("home")
//    object UserProfile : Screens("profile")
//    object CalorieGoals : Screens("goals")
//    object Stats : Screens("stats")
//    object AddFood : Screens("add_food")
//}