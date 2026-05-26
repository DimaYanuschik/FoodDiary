// MainActivity.kt - исправленная версия
package com.example.fooddiary

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.fooddiary.data_old.models.BarcodeScanResult
import com.example.fooddiary.data_old.repository.UserProfileRepository
import com.example.fooddiary.presentation.screens.auth.AuthViewModel
import com.example.fooddiary.presentation.screens.auth.LoginScreen
import com.example.fooddiary.presentation.screens.auth.RegisterScreen
import com.example.fooddiary.presentation.screens.foodrecognition.FoodRecognitionScreen
import com.example.fooddiary.presentation.screens.search.SearchScreen
import com.example.fooddiary.ui.screens.barcode.BarcodeProductScreen
import com.example.fooddiary.ui.screens.barcode.BarcodeScannerScreen
import com.example.fooddiary.ui.screens.camera.CameraScreen
import com.example.fooddiary.ui.screens.camera.GalleryPickerScreen
import com.example.fooddiary.ui.screens.food.AddFoodScreen
import com.example.fooddiary.ui.screens.main.HomeScreen
import com.example.fooddiary.ui.screens.profile.CalorieGoalScreen
import com.example.fooddiary.ui.screens.profile.UserProfileScreen
import com.example.fooddiary.ui.screens.stats.EnhancedStatsScreen
import com.example.fooddiary.ui.theme.FoodDiaryTheme
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import java.io.File

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

private fun uriToBitmap(uri: Uri, context: Context): Bitmap {
    return MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
}

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.authState.collectAsState()

    // Состояние для проверки наличия профиля
    var hasProfile by remember { mutableStateOf(false) }
    var isCheckingProfile by remember { mutableStateOf(true) }
    var lastCheckedUserId by remember { mutableStateOf<String?>(null) }

    // Проверяем наличие профиля при изменении authState
    LaunchedEffect(authState) {
        if (authState != null) {
            val userId = authState!!.uid
            // Проверяем только если userId изменился
            if (lastCheckedUserId != userId) {
                lastCheckedUserId = userId
                val repository = UserProfileRepository()
                val profile = try {
                    runBlocking { repository.getUserProfile(userId) }
                } catch (e: Exception) {
                    null
                }
                hasProfile = profile != null
            }
        } else {
            // Пользователь вышел - сбрасываем состояние
            hasProfile = false
            lastCheckedUserId = null
        }
        isCheckingProfile = false
    }

    // Определяем стартовый экран
    val startDestination = remember(authState, hasProfile, isCheckingProfile) {
        when {
            isCheckingProfile -> "login" // Пока проверяем, показываем login
            authState == null -> "login"
            hasProfile -> "home"  // Если профиль есть - сразу на главный
            else -> "profile_setup"  // Если профиля нет - на создание
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

        // Profile Setup Screen после регистрации
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
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            HomeScreen(
                onLogout = {
                    authViewModel.signOut()
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onNavigateToCamera = { navController.navigate("camera") },
//                onNavigateToGallery = { navController.navigate("gallery")},
                onNavigateToGallery = { navController.navigate("gallery_recognition")},
                onNavigateToAddFood = { navController.navigate("add_food")},
                onNavigateToStats = { navController.navigate("stats") },
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToGoals = { navController.navigate("goals") },
                onNavigateToBarcodeScanner = { navController.navigate("barcode_scanner") },
                onNavigateToSearch = { navController.navigate("search") }
            )
        }

        // Камера
        composable("camera") {
            CameraScreen(
                onPhotoTaken = { uri ->
                    // После фото можно сразу перейти к добавлению еды, передав uri
                    navController.navigate("add_food?photoUri=${uri}") {
                        popUpTo("camera") { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Галерея
        composable("gallery") {
            GalleryPickerScreen(
                onImageSelected = { uri ->
                    navController.navigate("add_food?photoUri=${uri}") {
                        popUpTo("gallery") { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("gallery_recognition") {
            val context = LocalContext.current
            GalleryPickerScreen(
                onImageSelected = { uri ->
                    // Конвертируем URI → Bitmap → сохраняем во временный файл
                    val bitmap = uriToBitmap(uri, context)
                    val tempFile = File(context.cacheDir, "food_${System.currentTimeMillis()}.jpg")
                    tempFile.outputStream().use { out ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                    }
                    // Переходим на экран распознавания
//                    val filePath = tempFile.absolutePath.removePrefix("/")
//                    navController.navigate("food_recognition/$filePath") {
//                        popUpTo("gallery_recognition") { inclusive = true }
//                    }
                    val encodedPath = Uri.encode(tempFile.absolutePath)
                    navController.navigate("food_recognition/$encodedPath") {
                        popUpTo("gallery_recognition") { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Добавление еды (опционально с photoUri)
        composable(
            "add_food?photoUri={photoUri}",
            arguments = listOf(navArgument("photoUri") { type = NavType.StringType; defaultValue = "" })
        ) { backStackEntry ->
            val photoUri = backStackEntry.arguments?.getString("photoUri") ?: ""
            val homeEntry = navController.getBackStackEntry("home")

            AddFoodScreen(
                onNavigateBack = { navController.popBackStack() },
                onFoodAdded = { navController.popBackStack() },
                sharedViewModelStoreOwner = homeEntry
                // При необходимости передать photoUri внутрь AddFoodScreen

            )
        }

        // Статистика
        composable("stats") {
            val homeEntry = navController.getBackStackEntry("home")

            EnhancedStatsScreen(
                onNavigateBack = { navController.popBackStack()},
                sharedViewModelStoreOwner = homeEntry
            )
        }

        // Профиль пользователя (просмотр/редактирование)
        composable("profile") {
            val homeEntry = navController.getBackStackEntry("home")
            UserProfileScreen(
                onComplete = { navController.popBackStack() },
                onNavigateBack = { navController.popBackStack() },
                sharedViewModelStoreOwner = homeEntry
            )
        }

        // Цели по калориям
        composable("goals") {
            val homeEntry = navController.getBackStackEntry("home")

            CalorieGoalScreen(
                onComplete = { navController.popBackStack() },
                onNavigateBack = { navController.popBackStack() },
                sharedViewModelStoreOwner = homeEntry
            )
        }

        // Сканер штрихкода
        composable("barcode_scanner") {
            BarcodeScannerScreen(
                onProductFound = { result ->
                    // Передаём штрихкод как аргумент в экран продукта
                    navController.navigate("barcode_product/${result.barcode}") {
                        popUpTo("barcode_scanner") { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Экран информации о продукте по штрихкоду
        composable(
            "barcode_product/{barcode}",
            arguments = listOf(navArgument("barcode") { type = NavType.StringType })
        ) { backStackEntry ->
            val barcode = backStackEntry.arguments?.getString("barcode") ?: ""
            // Создаём BarcodeScanResult с barcode, сам продукт будет загружен внутри экрана
            val scanResult = BarcodeScanResult(barcode = barcode)
            BarcodeProductScreen(
                scanResult = scanResult,
                onAddToDiary = { navController.popBackStack() },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Экран поиска продуктов
        composable("search") {
            SearchScreen(
                onProductClick = { productId ->
                    // Действие при клике на продукт: например, добавить в дневник или показать детали
                    // Пока просто возвращаемся назад, позже можно расширить
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable("food_recognition/{imagePath}",
            arguments = listOf(navArgument("imagePath") { type = NavType.StringType })
        ) { backStackEntry ->
//            val imagePath = "/" + (backStackEntry.arguments?.getString("imagePath") ?: "")
//
//            val bitmap = remember(imagePath) {
//                BitmapFactory.decodeFile(imagePath)
//            }
            val encodedPath = backStackEntry.arguments?.getString("imagePath") ?: ""
            val imagePath = Uri.decode(encodedPath)
            val bitmap = remember(imagePath) {
                BitmapFactory.decodeFile(imagePath)
            }
            val homeEntry = navController.getBackStackEntry("home")
            FoodRecognitionScreen(
                onNavigateBack = { navController.popBackStack() },
                onFoodAdded = { navController.popBackStack() },
                selectedImageBitmap = bitmap,
                sharedViewModelStoreOwner = homeEntry
            )
        }
    }
}