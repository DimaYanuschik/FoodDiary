package com.example.fooddiary.presentation.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fooddiary.domain.usecase.product.AddSearchQueryUseCase
import com.example.fooddiary.domain.usecase.product.GetSearchHistoryUseCase
import com.example.fooddiary.domain.usecase.product.SearchProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchProductsUseCase: SearchProductsUseCase,
    private val getSearchHistoryUseCase: GetSearchHistoryUseCase,
    private val addSearchQueryUseCase: AddSearchQueryUseCase
): ViewModel() {
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            try {
                val history = getSearchHistoryUseCase()
                _uiState.value = _uiState.value.copy(history = history)
            } catch (_: Exception) {
                // Игнорируем ошибку загрузки истории
            }
        }
    }

    fun onQueryChanged(newQuery: String) {
        _uiState.value = _uiState.value.copy(query = newQuery)

        // Запускаем поиск при каждом изменении текста с небольшой задержкой? TODO: добавить дебаунс 300мс
        // Для простоты будем искать сразу, но можно добавить debounce позже.

        search(newQuery)
    }

    private fun search(query: String) {
        viewModelScope.launch {
            if (query.isBlank() || query.length < 2) { // TODO: Валидацию как будто бы лучше вынести в юз кейсы
                _uiState.value = _uiState.value.copy(products = emptyList(), isLoading = false, error = null)
                return@launch
            }
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val products = searchProductsUseCase(query)
                _uiState.value = _uiState.value.copy(products = products, isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message ?: "Ошибка поиска", isLoading = false)
            }
        }
    }

    fun onSearchSubmit(query: String) {
        search(query)
        // Сохраняем запрос в историю после явной отправки (TODO: только после успешного поиска)
        viewModelScope.launch {
            addSearchQueryUseCase(query)
            loadHistory()
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}