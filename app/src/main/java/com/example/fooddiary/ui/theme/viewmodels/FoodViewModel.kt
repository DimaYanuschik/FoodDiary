package com.example.fooddiary.ui.viewmodels

import android.util.Log
import androidx.compose.runtime.currentCompositionErrors
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fooddiary.data_old.models.ScannedFoodEntry
import com.example.fooddiary.data_old.repository.FoodEntry
import com.example.fooddiary.data_old.repository.FoodRepository
import com.example.fooddiary.data_old.repository.DailyStats
import com.example.fooddiary.domain.repository.auth.IAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class FoodViewModel @Inject constructor(
    private val foodRepository: FoodRepository,
    private val authRepository: IAuthRepository
) : ViewModel() {

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

    private val _selectedDate = MutableStateFlow(Date())
    val selectedDate: StateFlow<Date> = _selectedDate.asStateFlow()

    private val _selectedDateEntries = MutableStateFlow<List<FoodEntry>>(emptyList())
    val selectedDateEntries: StateFlow<List<FoodEntry>> = _selectedDateEntries.asStateFlow()

    private val currentUserId: String? = authRepository.getCurrentUser()?.uid

    init {
        loadAllData()
    }

    fun selectDate(date: Date) {
        Log.d("Date", "new date: $date")
        if (_selectedDate.value != date) {
            _selectedDate.value = date
            loadAllData()
        }
    }

//    init {
//        viewModelScope.launch {
//            currentUserId?.let { userId ->
//                foodRepository.testQueries(userId)
//            }
//        }
//    }


//    fun setUserId(userId: String) {
//        Log.d("FoodViewModel", "setUserId: $userId")
//        if (currentUserId != userId) {
//            currentUserId = userId
//            loadAllData()
//        }
//    }

    fun loadAllData() {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                _isLoading.value = true
                try {
                    val date = _selectedDate.value
                    // записи на дату
                    val entries = foodRepository.getFoodEntriesByDate(userId, date)
                    _selectedDateEntries.value = entries
                    _dailyStats.value = calculateDailyStats(entries, date)

                    // неделя, содержащая date(с Пн по Вс)
                    val weekStart = getWeekStart(date)
                    _weeklyStats.value = foodRepository.getDailyStatsForWeek(userId, weekStart)

                    // все записи для других нужд
                    _foodEntries.value = foodRepository.getFoodEntries(userId)
                } catch (e: Exception) {
                    _errorMessage.value = "Ошибка загрузки: ${e.message}"
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }

//    fun loadAllData() {
//        currentUserId?.let { userId ->
//            viewModelScope.launch {
//                _isLoading.value = true
//                Log.d("FoodViewModel", "Начинаем загрузку всех данных для userId: $userId")
//                try {
//                    // Загружаем сегодняшние записи
//                    loadTodayData(userId)
//
//                    // Загружаем статистику за неделю
//                    val weekly = foodRepository.getDailyStatsForWeek(userId)
//                    Log.d("FoodViewModel", "Статистика за неделю: дней=${weekly.size}")
//                    _weeklyStats.value = weekly
//
//                } catch (e: Exception) {
//                    Log.e("FoodViewModel", "Ошибка загрузки данных: ${e.message}", e)
//                    _errorMessage.value = "Ошибка загрузки данных: ${e.message}"
//                } finally {
//                    _isLoading.value = false
//                    Log.d("FoodViewModel", "Загрузка завершена")
//                }
//            }
//        } ?: run {
//            Log.e("FoodViewModel", "currentUserId is null!")
//        }
//    }

//    private suspend fun loadTodayData(userId: String) {
//        // Загружаем сегодняшние записи
//        val todayEntries = foodRepository.getTodayFoodEntries(userId)
//        Log.d("FoodViewModel", "Загружено сегодняшних записей: ${todayEntries.size}")
//        todayEntries.forEach { entry ->
//            Log.d("FoodViewModel", "Сегодня: ${entry.name}, ${entry.calories} ккал")
//        }
//        _todayFoodEntries.value = todayEntries
//
//        // Рассчитываем статистику за сегодня
//        val stats = calculateDailyStats(todayEntries)
//        Log.d("FoodViewModel", "Статистика за сегодня: ${stats.totalCalories} ккал")
//        _dailyStats.value = stats
//
//        // Также загружаем все записи для других экранов
//        val allEntries = foodRepository.getFoodEntries(userId)
//        _foodEntries.value = allEntries
//    }

//    private fun calculateDailyStats(entries: List<FoodEntry>): DailyStats {
    private fun calculateDailyStats(entries: List<FoodEntry>, date: Date): DailyStats {
        val totalCalories = entries.sumOf { it.calories }
        val totalProtein = entries.sumOf { it.protein }
        val totalFat = entries.sumOf { it.fat }
        val totalCarbs = entries.sumOf { it.carbs }

        return DailyStats(
//            date = Date(),
            date = date,
            totalCalories = totalCalories,
            totalProtein = totalProtein,
            totalFat = totalFat,
            totalCarbs = totalCarbs,
            userId = currentUserId ?: ""
        )
    }

    private fun getWeekStart(date: Date): Date {
        val cal = Calendar.getInstance().apply { time = date }
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.time
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
//                        date = scannedEntry.date,
                        date = scannedEntry.date,
                        userId = userId,
                        mealType = scannedEntry.mealType
                    )

                    foodRepository.addFoodEntry(entryToSave, userId)
//                    loadTodayData(userId)
                    loadAllData()
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
//                    loadTodayData(userId)
                    loadAllData()
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


    // Продукт, выбранный из поиска для быстрого добавления
    private val _selectedSearchProduct = MutableStateFlow<com.example.fooddiary.domain.model.product.Product?>(null)
    val selectedSearchProduct: StateFlow<com.example.fooddiary.domain.model.product.Product?> = _selectedSearchProduct.asStateFlow()

    fun setProductFromSearch(product: com.example.fooddiary.domain.model.product.Product) {
        _selectedSearchProduct.value = product
    }

    fun clearSearchProduct() {
        _selectedSearchProduct.value = null
    }
}