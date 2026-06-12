package com.example.fooddiary.presentation.screens.search

import android.R
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.room.util.query
import com.example.fooddiary.ui.theme.WireframeTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen (
    onProductSelected: (com.example.fooddiary.domain.model.product.Product) -> Unit,
    onBack: () -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Назад")
            }
            Text(
                text = "Поиск продуктов",
                style = MaterialTheme.typography.titleMedium
            )

        }

        // Поисковая строка
        SearchBar(
            query = state.query,
            onQueryChange = {viewModel.onQueryChanged(it)},
            onSearch = {
                viewModel.onSearchSubmit(it)
                Log.d("SearchRepo", "Searching...")
            },
            active = false,
            onActiveChange = {},
            placeholder = { Text("Поиск продуктов...")},
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Поиск") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            // Здесь можно показывать подсказки (историю), но SearchBar имеет встроенный список
            // Пока оставим пустым, историю покажем отдельно.
        }

        // Ошибка
        if (state.error != null) {
            Text(
                text = state.error!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // Индикатор загрузки
        if (state.isLoading) {
            LinearProgressIndicator(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
            )
        }

        // История (показываем, если запрос пустой и есть история)
        if (state.query.isBlank() && state.history.isNotEmpty()) {
            Text(
                text = "Недавние запросы",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(state.history) { query ->
                    ListItem(
                        headlineContent = { Text(query) },
                        modifier = Modifier.clickable {
                            viewModel.onQueryChanged(query)
                            viewModel.onSearchSubmit(query)
                        }
                    )
                }
            }
        }

        // Результаты поиска
        if (state.products.isNotEmpty()) {
            Text(
                text = "Результаты",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(state.products) { product ->
                    ListItem(
                        headlineContent = { Text(product.name)},
                        supportingContent = {
                            if (product.brand != null) Text(product.brand)
                        },
                        trailingContent = {
                            Text("${product.caloriesPer100g.toInt()} ккал")
                        },
                        modifier = Modifier.clickable {
//                            onProductClick(product.id)
                            onProductSelected(product)

                        }
                    )
                }
            }
        }
    }
}

