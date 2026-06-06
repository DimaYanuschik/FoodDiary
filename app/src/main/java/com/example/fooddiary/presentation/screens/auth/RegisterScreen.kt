//// presentation/screens/auth/RegisterScreen.kt
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
//fun RegisterScreen(
//    onNavigateToLogin: () -> Unit,
//    onNavigateToProfileSetup: () -> Unit,
//    viewModel: AuthViewModel
//) {
//    val uiState by viewModel.registerUiState.collectAsState()
//    val authState by viewModel.authState.collectAsState()
//
//    // Обработка изменения состояния авторизации
//    LaunchedEffect(authState) {
//        if (authState != null) {
//            onNavigateToProfileSetup()
//        }
//    }
//
//    LaunchedEffect(Unit) {
//        viewModel.clearForms()
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(24.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        Text(
//            text = "Создание аккаунта",
//            style = MaterialTheme.typography.headlineLarge
//        )
//
//        Spacer(modifier = Modifier.height(32.dp))
//
//        OutlinedTextField(
//            value = uiState.email,
//            onValueChange = viewModel::updateRegisterEmail,
//            label = { Text("Email") },
//            modifier = Modifier.fillMaxWidth(),
//            enabled = !uiState.isLoading,
//            isError = uiState.errorMessage != null
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        OutlinedTextField(
//            value = uiState.password,
//            onValueChange = viewModel::updateRegisterPassword,
//            label = { Text("Пароль") },
//            modifier = Modifier.fillMaxWidth(),
//            visualTransformation = PasswordVisualTransformation(),
//            enabled = !uiState.isLoading
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        OutlinedTextField(
//            value = uiState.confirmPassword,
//            onValueChange = viewModel::updateRegisterConfirmPassword,
//            label = { Text("Подтвердите пароль") },
//            modifier = Modifier.fillMaxWidth(),
//            visualTransformation = PasswordVisualTransformation(),
//            enabled = !uiState.isLoading,
//            isError = uiState.password != uiState.confirmPassword && uiState.confirmPassword.isNotEmpty()
//        )
//
//        if (!uiState.errorMessage.isNullOrEmpty()) {
//            Text(
//                text = uiState.errorMessage!!,
//                color = MaterialTheme.colorScheme.error,
//                style = MaterialTheme.typography.bodySmall,
//                modifier = Modifier.padding(top = 16.dp)
//            )
//        }
//
//        if (uiState.password != uiState.confirmPassword && uiState.confirmPassword.isNotEmpty()) {
//            Text(
//                text = "Пароли не совпадают",
//                color = MaterialTheme.colorScheme.error,
//                style = MaterialTheme.typography.bodySmall,
//                modifier = Modifier.padding(top = 8.dp)
//            )
//        }
//
//        Spacer(modifier = Modifier.height(24.dp))
//
//        Button(
//            onClick = { viewModel.signUp() },
//            modifier = Modifier.fillMaxWidth(),
//            enabled = !uiState.isLoading &&
//                    uiState.password == uiState.confirmPassword &&
//                    uiState.password.isNotEmpty() &&
//                    uiState.email.isNotEmpty()
//        ) {
//            if (uiState.isLoading) {
//                CircularProgressIndicator(
//                    modifier = Modifier.size(16.dp),
//                    strokeWidth = 2.dp
//                )
//            } else {
//                Text("Зарегистрироваться")
//            }
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        TextButton(
//            onClick = onNavigateToLogin,
//            enabled = !uiState.isLoading
//        ) {
//            Text("Уже есть аккаунт? Войти")
//        }
//    }
//}

package com.example.fooddiary.presentation.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToProfileSetup: () -> Unit,
    viewModel: AuthViewModel
) {
    val uiState by viewModel.registerUiState.collectAsState()
    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState != null) {
            onNavigateToProfileSetup()
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
        Icon(
            imageVector = Icons.Filled.PersonAdd,
            contentDescription = "Регистрация",
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Создание аккаунта",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

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
                    onValueChange = viewModel::updateRegisterEmail,
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading,
                    singleLine = true,
                    isError = uiState.errorMessage != null
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = uiState.password,
                    onValueChange = viewModel::updateRegisterPassword,
                    label = { Text("Пароль") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    enabled = !uiState.isLoading,
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = uiState.confirmPassword,
                    onValueChange = viewModel::updateRegisterConfirmPassword,
                    label = { Text("Подтвердите пароль") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    enabled = !uiState.isLoading,
                    singleLine = true,
                    isError = uiState.password != uiState.confirmPassword && uiState.confirmPassword.isNotEmpty()
                )

                if (!uiState.errorMessage.isNullOrEmpty()) {
                    Text(
                        text = uiState.errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }

                if (uiState.password != uiState.confirmPassword && uiState.confirmPassword.isNotEmpty()) {
                    Text(
                        text = "Пароли не совпадают",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { viewModel.signUp() },
                    modifier = Modifier.fillMaxWidth(),
//                    enabled = !uiState.isLoading &&
//                            uiState.password == uiState.confirmPassword &&
//                            uiState.password.isNotEmpty() &&
//                            uiState.email.isNotEmpty(),
                    enabled = true,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Зарегистрироваться", style = MaterialTheme.typography.labelLarge)
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
//            enabled = false
        ) {
            Text("Войти через Google")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = onNavigateToLogin,
            enabled = !uiState.isLoading
        ) {
            Text("Уже есть аккаунт? Войти")
        }
    }
}