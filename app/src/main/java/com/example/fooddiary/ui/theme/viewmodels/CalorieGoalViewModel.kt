package com.example.fooddiary.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fooddiary.data.models.CalorieGoal
import com.example.fooddiary.data.repository.CalorieGoalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CalorieGoalViewModel : ViewModel() {
    private val calorieGoalRepository = CalorieGoalRepository()

    private val _calorieGoal = MutableStateFlow<CalorieGoal?>(null)
    val calorieGoal: StateFlow<CalorieGoal?> = _calorieGoal.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    var currentUserId: String? = null

    fun setUserId(userId: String) {
        if (currentUserId != userId) {
            currentUserId = userId
            loadCalorieGoal()
        }
    }

    fun loadCalorieGoal() {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                _isLoading.value = true
                try {
                    val goal = calorieGoalRepository.getCalorieGoal(userId)
                    _calorieGoal.value = goal
                } catch (e: Exception) {
                    _errorMessage.value = "Ошибка загрузки целей: ${e.message}"
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }

    fun saveCalorieGoal(goal: CalorieGoal) {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                _isLoading.value = true
                try {
                    val goalWithUserId = goal.copy(userId = userId)
                    calorieGoalRepository.saveCalorieGoal(goalWithUserId)
                    _calorieGoal.value = goalWithUserId
                } catch (e: Exception) {
                    _errorMessage.value = "Ошибка сохранения целей: ${e.message}"
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }

    fun getDefaultGoal(dailyCalories: Int): CalorieGoal {
        return CalorieGoal(
            dailyCalories = dailyCalories,
            proteinPercentage = 30,
            fatPercentage = 30,
            carbsPercentage = 40
        )
    }

    fun clearError() {
        _errorMessage.value = null
    }
}