package com.example.fooddiary.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fooddiary.data_old.models.ScannedFoodEntry
import com.example.fooddiary.data_old.repository.FoodEntry
import com.example.fooddiary.data_old.repository.FoodRepository
import com.example.fooddiary.data_old.repository.DailyStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class FoodViewModel @Inject constructor(
    private val foodRepository: FoodRepository
) : ViewModel() {
//class FoodViewModel() : ViewModel() {

//    private val foodRepository = FoodRepository()

    private val _foodEntries = MutableStateFlow<List<FoodEntry>>(emptyList())
    val foodEntries: StateFlow<List<FoodEntry>> = _foodEntries.asStateFlow()

    private val _todayFoodEntries = MutableStateFlow<List<FoodEntry>>(emptyList())
    val todayFoodEntries: StateFlow<List<FoodEntry>> = _todayFoodEntries.asStateFlow()

    private val _dailyStats = MutableStateFlow(DailyStats())
    val dailyStats: StateFlow<DailyStats> = _dailyStats.asStateFlow()

    private val _weeklyStats = MutableStateFlow<List<DailyStats>>(emptyList())
    val weeklyStats: StateFlow<List<DailyStats>> = _weeklyStats.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    var currentUserId: String? = null

    init {
        viewModelScope.launch {
            currentUserId?.let { userId ->
                foodRepository.testQueries(userId)
            }
        }
    }


    fun setUserId(userId: String) {
        Log.d("FoodViewModel", "setUserId: $userId")
        if (currentUserId != userId) {
            currentUserId = userId
            loadAllData()
        }
    }

    fun loadAllData() {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                _isLoading.value = true
                Log.d("FoodViewModel", "Начинаем загрузку всех данных для userId: $userId")
                try {
                    // Загружаем сегодняшние записи
                    loadTodayData(userId)

                    // Загружаем статистику за неделю
                    val weekly = foodRepository.getDailyStatsForWeek(userId)
                    Log.d("FoodViewModel", "Статистика за неделю: дней=${weekly.size}")
                    _weeklyStats.value = weekly

                } catch (e: Exception) {
                    Log.e("FoodViewModel", "Ошибка загрузки данных: ${e.message}", e)
                    _errorMessage.value = "Ошибка загрузки данных: ${e.message}"
                } finally {
                    _isLoading.value = false
                    Log.d("FoodViewModel", "Загрузка завершена")
                }
            }
        } ?: run {
            Log.e("FoodViewModel", "currentUserId is null!")
        }
    }

    private suspend fun loadTodayData(userId: String) {
        // Загружаем сегодняшние записи
        val todayEntries = foodRepository.getTodayFoodEntries(userId)
        Log.d("FoodViewModel", "Загружено сегодняшних записей: ${todayEntries.size}")
        todayEntries.forEach { entry ->
            Log.d("FoodViewModel", "Сегодня: ${entry.name}, ${entry.calories} ккал")
        }
        _todayFoodEntries.value = todayEntries

        // Рассчитываем статистику за сегодня
        val stats = calculateDailyStats(todayEntries)
        Log.d("FoodViewModel", "Статистика за сегодня: ${stats.totalCalories} ккал")
        _dailyStats.value = stats

        // Также загружаем все записи для других экранов
        val allEntries = foodRepository.getFoodEntries(userId)
        _foodEntries.value = allEntries
    }

    private fun calculateDailyStats(entries: List<FoodEntry>): DailyStats {
        val totalCalories = entries.sumOf { it.calories }
        val totalProtein = entries.sumOf { it.protein }
        val totalFat = entries.sumOf { it.fat }
        val totalCarbs = entries.sumOf { it.carbs }

        return DailyStats(
            date = Date(),
            totalCalories = totalCalories,
            totalProtein = totalProtein,
            totalFat = totalFat,
            totalCarbs = totalCarbs,
            userId = currentUserId ?: ""
        )
    }

//    fun addFoodEntry(food: FoodEntry) {
//        currentUserId?.let { userId ->
//            viewModelScope.launch {
//                _isLoading.value = true
//                Log.d("FoodViewModel", "Добавляем запись: ${food.name}")
//                try {
//                    val id = foodRepository.addFoodEntry(food, userId)
//                    Log.d("FoodViewModel", "Запись добавлена с ID: $id")
//
//                    // После добавления обновляем только сегодняшние данные
//                    loadTodayData(userId)
//
//                } catch (e: Exception) {
//                    Log.e("FoodViewModel", "Ошибка добавления: ${e.message}", e)
//                    _errorMessage.value = "Ошибка добавления: ${e.message}"
//                } finally {
//                    _isLoading.value = false
//                }
//            }
//        } ?: run {
//            Log.e("FoodViewModel", "Не могу добавить запись: currentUserId is null!")
//        }
//    }

    fun addFoodEntry(scannedEntry: ScannedFoodEntry) {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                _isLoading.value = true
                try {
                    // Маппинг ScannedFoodEntry -> FoodEntry
                    val entryToSave = FoodEntry(
                        id = scannedEntry.id.ifEmpty { UUID.randomUUID().toString() },
                        name = scannedEntry.name,
                        calories = scannedEntry.calories,
                        protein = scannedEntry.protein,
                        fat = scannedEntry.fat,
                        carbs = scannedEntry.carbs,
                        date = scannedEntry.date,
                        userId = userId,
                        mealType = scannedEntry.mealType
                    )

                    foodRepository.addFoodEntry(entryToSave, userId)
                    loadTodayData(userId)
                } catch (e: Exception) {
                    Log.e("FoodViewModel", "Ошибка добавления: ${e.message}")
                    _errorMessage.value = e.message
                } finally {
                    _isLoading.value = false
                }
            }
        } ?: run {
            Log.e("FoodViewModel", "Не могу добавить запись: currentUserId is null!")
        }
    }

    fun deleteFoodEntry(foodId: String) {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                _isLoading.value = true
                try {
                    foodRepository.deleteFoodEntry(foodId)
                    // После удаления обновляем данные
                    loadTodayData(userId)
                } catch (e: Exception) {
                    _errorMessage.value = "Ошибка удаления: ${e.message}"
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }

    fun refreshData() {
        loadAllData()
    }

    suspend fun TestQueries() {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                foodRepository.testQueries(userId)
            }
        }
    }

    fun getWeeklyDataForChart(): List<Pair<Date, Int>> {
        return _weeklyStats.value.map { it.date to it.totalCalories }
    }
}