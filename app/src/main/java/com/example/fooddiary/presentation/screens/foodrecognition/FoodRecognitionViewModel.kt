package com.example.fooddiary.presentation.screens.foodrecognition

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fooddiary.domain.model.foodrecognition.FoodRecognitionResult
import com.example.fooddiary.domain.usecase.foodrecognition.RecognizeFoodUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FoodRecognitionUiState(
    val selectedImageBitmap: Bitmap? = null,
    val result: FoodRecognitionResult? = null,
    val editableResult: FoodRecognitionResult? = null,  // то, что пользователь может редактировать
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class FoodRecognitionViewModel @Inject constructor(
    private val recognizeFoodUseCase: RecognizeFoodUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FoodRecognitionUiState())
    val uiState: StateFlow<FoodRecognitionUiState> = _uiState.asStateFlow()

    private var currentBitmap: Bitmap? = null

    fun onImageSelected(bitmap: Bitmap) {
        currentBitmap = bitmap
        _uiState.value = _uiState.value.copy(
            selectedImageBitmap = bitmap,
            result = null,
            editableResult = null,
            error = null
        )
        recognizeFood(bitmap)
    }

    // Ручное редактирование полей
    fun updateEditableResult(result: FoodRecognitionResult) {
        _uiState.value = _uiState.value.copy(editableResult = result)
    }

    // Уточняющий запрос к ИИ
    fun refineWithText(query: String) {
        val bitmap = currentBitmap ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            // Вызываем use case с дополнительным текстом
            recognizeFoodUseCase.refine(bitmap, query)
                .onSuccess { result ->
                    _uiState.value = _uiState.value.copy(
                        result = result,
                        editableResult = result,
                        isLoading = false
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message ?: "Ошибка уточнения",
                        isLoading = false
                    )
                }
        }
    }

    private fun recognizeFood(bitmap: Bitmap) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            recognizeFoodUseCase(bitmap)
                .onSuccess { result ->
                    _uiState.value = _uiState.value.copy(
                        result = result,
                        editableResult = result,  // копия для редактирования
                        isLoading = false
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message ?: "Неизвестная ошибка",
                        isLoading = false
                    )
                }
        }
    }

    fun clear() {
        _uiState.value = FoodRecognitionUiState()
        currentBitmap = null
    }
}