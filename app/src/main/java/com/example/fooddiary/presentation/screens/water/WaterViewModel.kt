package com.example.fooddiary.presentation.screens.water

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fooddiary.domain.model.water.WaterEntry
import com.example.fooddiary.domain.repository.auth.IAuthRepository
import com.example.fooddiary.domain.usecase.water.AddWaterUseCase
import com.example.fooddiary.domain.usecase.water.DeleteWaterEntryUseCase
import com.example.fooddiary.domain.usecase.water.GetWaterGoalUseCase
import com.example.fooddiary.domain.usecase.water.GetWaterProgressUseCase
import com.example.fooddiary.domain.usecase.water.SetWaterGoalUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject


data class WaterUiState(
    val entries: List<WaterEntry> = emptyList(),
    val totalMl: Int = 0,
    val goalMl: Int = 2000,
    val isLoading: Boolean = false
)

@HiltViewModel
class WaterViewModel @Inject constructor(
    private val addWaterUseCase: AddWaterUseCase,
    private val getProgressUseCase: GetWaterProgressUseCase,
    private val deleteEntryUseCase: DeleteWaterEntryUseCase,
    private val getGoalUseCase: GetWaterGoalUseCase,
    private val setGoalUseCase: SetWaterGoalUseCase,
    private val authRepository: IAuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(WaterUiState())
    val uiState: StateFlow<WaterUiState> = _uiState.asStateFlow()

    private var currentDate: Date = Date()
    private val userId: String? = authRepository.getCurrentUser()?.uid

    init {
        loadGoal()
    }

    fun loadForDate(date: Date) {
        currentDate = date
        loadProgress()
    }

    fun addWater(amountMl: Int) {
        val uid = userId ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                addWaterUseCase(amountMl, currentDate, uid)
                loadProgress()
            } catch (e: Exception) {
                // можно обработать ошибку
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun deleteEntry(entryId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                deleteEntryUseCase(entryId)
                loadProgress()
            } catch (_: Exception) {
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun setGoal(goalMl: Int) {
        val uid = userId ?: return
        viewModelScope.launch {
            try {
                setGoalUseCase(uid, goalMl)
                _uiState.value = _uiState.value.copy(goalMl = goalMl)
            } catch (_: Exception) {}
        }
    }

    private fun loadProgress() {
        val uid = userId ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val entries = getProgressUseCase(uid, currentDate)
                _uiState.value = _uiState.value.copy(
                    entries = entries,
                    totalMl = entries.sumOf { it.amountMl },
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    private fun loadGoal() {
        val uid = userId ?: return
        viewModelScope.launch {
            try {
                val goal = getGoalUseCase(uid)
                if (goal > 0) {
                    _uiState.value = _uiState.value.copy(goalMl = goal)
                }
            } catch (_: Exception) {}
        }
    }
}