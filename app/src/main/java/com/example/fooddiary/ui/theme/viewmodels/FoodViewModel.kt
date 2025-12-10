package com.example.fooddiary.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fooddiary.data.repository.FoodEntry
import com.example.fooddiary.data.repository.FoodRepository
import com.example.fooddiary.data.repository.DailyStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

class FoodViewModel : ViewModel() {
    private val foodRepository = FoodRepository()

    private val _foodEntries = MutableStateFlow<List<FoodEntry>>(emptyList())
    val foodEntries: StateFlow<List<FoodEntry>> = _foodEntries.asStateFlow()

    private val _dailyStats = MutableStateFlow(DailyStats())
    val dailyStats: StateFlow<DailyStats> = _dailyStats.asStateFlow()

    private val _weeklyStats = MutableStateFlow<List<DailyStats>>(emptyList())
    val weeklyStats: StateFlow<List<DailyStats>> = _weeklyStats.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    var currentUserId: String? = null // was public

    fun setUserId(userId: String) {
        currentUserId = userId
        loadData()
    }

    public fun loadData() { // private
        currentUserId?.let { userId ->
            viewModelScope.launch {
                _isLoading.value = true
                try {
                    val entries = foodRepository.getFoodEntries(userId)
                    _foodEntries.value = entries

                    val stats = foodRepository.getDailyStats(userId, Date())
                    _dailyStats.value = stats

                    val weekly = foodRepository.getDailyStatsForWeek(userId)
                    _weeklyStats.value = weekly
                } catch (e: Exception) {
                    _errorMessage.value = "Ошибка загрузки данных: ${e.message}"
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }

    fun addFoodEntry(food: FoodEntry) {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                _isLoading.value = true
                try {
                    foodRepository.addFoodEntry(food, userId)
                    loadData() // Перезагружаем данные
                } catch (e: Exception) {
                    _errorMessage.value = "Ошибка добавления: ${e.message}"
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }

    fun deleteFoodEntry(foodId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                foodRepository.deleteFoodEntry(foodId)
                loadData() // Перезагружаем данные
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка удаления: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshData() {
        loadData()
    }
}