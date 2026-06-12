//package com.example.fooddiary.presentation.screens.auth
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.text.input.PasswordVisualTransformation
//import androidx.compose.ui.unit.dp
//
//@Composable
//fun LoginScreen(
//    onNavigateToRegister: () -> Unit,
//    onNavigateToProfileSetup: () -> Unit,
//    onNavigateToHome: () -> Unit,
//    viewModel: AuthViewModel
//) {
//    val uiState by viewModel.loginUiState.collectAsState()
//    val authState by viewModel.authState.collectAsState()
//
//    LaunchedEffect(authState) {
//        when {
//            authState != null && authState!!.isEmailVerified -> onNavigateToHome()
//            authState != null && !authState!!.isEmailVerified -> onNavigateToProfileSetup()
//        }
//    }
//
//    LaunchedEffect(Unit) {
//        viewModel.clearForms()
//    }
//
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(24.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        Text(
//            text = "Food Diary",
//            style = MaterialTheme.typography.headlineLarge
//        )
//
//        Text(
//            text = "Трекер питания",
//            style = MaterialTheme.typography.bodyMedium,
//            modifier = Modifier.padding(top = 8.dp)
//        )
//
//        Spacer(modifier = Modifier.height(32.dp))
//
//        OutlinedTextField(
//            value = uiState.email,
//            onValueChange = viewModel::updateLoginEmail,
//            label = { Text("Email") },
//            modifier = Modifier.fillMaxWidth(),
//            enabled = !uiState.isLoading
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        OutlinedTextField(
//            value = uiState.password,
//            onValueChange = viewModel::updateLoginPassword,
//            label = { Text("Пароль") },
//            modifier = Modifier.fillMaxWidth(),
//            visualTransformation = PasswordVisualTransformation(),
//            enabled = !uiState.isLoading
//        )
//
//        if (!uiState.errorMessage.isNullOrEmpty()) {
//            Text(
//                text = uiState.errorMessage!!,
//                color = MaterialTheme.colorScheme.error,
//                modifier = Modifier.padding(top = 16.dp)
//            )
//        }
//
//        Spacer(modifier = Modifier.height(24.dp))
//
//        Button(
//            onClick = { viewModel.signIn() },
//            modifier = Modifier.fillMaxWidth(),
//            enabled = !uiState.isLoading
//        ) {
//            if (uiState.isLoading) {
//                CircularProgressIndicator(
//                    modifier = Modifier.size(16.dp),
//                    strokeWidth = 2.dp
//                )
//            } else {
//                Text("Войти")
//            }
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        OutlinedButton(
//            onClick = onNavigateToRegister,
//            modifier = Modifier.fillMaxWidth(),
//            enabled = !uiState.isLoading
//        ) {
//            Text("Создать аккаунт")
//        }
//    }
//}



package com.example.fooddiary.presentation.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp





import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.fooddiary.ui.theme.WireframeTheme

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview_Wireframe() {
    WireframeTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Логотип-заглушка (круг)
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(Color.DarkGray, CircleShape)
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Название приложения
            Text(
                text = "Food Diary",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.Black
            )
            Text(
                text = "Ассистент по питанию",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Карточка с полями
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFEEEEEE), RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Поле Email
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                            .padding(start = 12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = "Email",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

// Поле Пароль
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                            .padding(start = 12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = "Пароль",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Кнопка "Войти"
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .background(Color.DarkGray, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Войти",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Разделитель "или"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f).height(1.dp).background(Color.LightGray))
                Text(
                    text = "  или  ",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )
                Box(modifier = Modifier.weight(1f).height(1.dp).background(Color.LightGray))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Кнопка Google
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Войти через Google",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.DarkGray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Кнопка "Создать аккаунт"
            TextButton(onClick = {}) {
                Text(
                    text = "Создать аккаунт",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.DarkGray
                )
            }
        }
    }
}


@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToProfileSetup: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: AuthViewModel
) {
    val uiState by viewModel.loginUiState.collectAsState()
    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        when {
            authState != null && authState!!.isEmailVerified -> onNavigateToHome()
            authState != null && !authState!!.isEmailVerified -> onNavigateToProfileSetup()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.clearForms()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Логотип
        Icon(
            imageVector = Icons.Filled.Restaurant,
            contentDescription = "Логотип",
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Food Diary",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Ассистент по питанию",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Карточка с полями ввода
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = viewModel::updateLoginEmail,
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading,
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = uiState.password,
                    onValueChange = viewModel::updateLoginPassword,
                    label = { Text("Пароль") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    enabled = !uiState.isLoading,
                    singleLine = true
                )

                if (!uiState.errorMessage.isNullOrEmpty()) {
                    Text(
                        text = uiState.errorMessage!!,
//                        text = "Неверный email или пароль",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { viewModel.signIn() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Войти", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Разделитель "или"
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text(
                text = "  или  ",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            HorizontalDivider(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Кнопка Google (неактивная)
        OutlinedButton(
            onClick = { /* не реализовано */ },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
//            enabled = false   // пока не работает
        ) {
            // Простая иконка Google (текст + восклицательный знак, позже можно заменить на иконку)
            Text("Войти через Google")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Регистрация
        TextButton(
            onClick = onNavigateToRegister,
            enabled = !uiState.isLoading
        ) {
            Text("Создать аккаунт")
        }
    }
}



