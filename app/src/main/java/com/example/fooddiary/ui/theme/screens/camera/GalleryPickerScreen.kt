package com.example.fooddiary.ui.screens.camera

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryPickerScreen(
    onImageSelected: (Uri) -> Unit,
    onNavigateBack: () -> Unit
) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher для выбора одного изображения
    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            selectedImageUri = it
            onImageSelected(it)
        }
    }

    // Launcher для выбора нескольких изображений
    val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris ->
        uris.firstOrNull()?.let {
            selectedImageUri = it
            onImageSelected(it)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Выберите фото из галереи") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        },
        floatingActionButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                // Кнопка выбора одного фото
                ExtendedFloatingActionButton(
                    onClick = {
                        singlePhotoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    icon = { Icon(Icons.Filled.Photo, contentDescription = "Выбрать фото") },
                    text = { Text("Выбрать фото") }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (selectedImageUri != null) {
                // Превью выбранного изображения
                AsyncImage(
                    model = selectedImageUri,
                    contentDescription = "Выбранное фото",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Фото выбрано!",
                    style = MaterialTheme.typography.titleMedium
                )
            } else {
                // Инструкция
                Icon(
                    Icons.Filled.PhotoLibrary,
                    contentDescription = "Галерея",
                    modifier = Modifier.size(100.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Нажмите кнопку ниже чтобы выбрать фото еды из галереи",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}