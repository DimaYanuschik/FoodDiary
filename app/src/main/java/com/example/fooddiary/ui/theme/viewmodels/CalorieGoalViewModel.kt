package com.example.fooddiary.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fooddiary.data_old.models.CalorieGoal
import com.example.fooddiary.data_old.repository.CalorieGoalRepository
import com.example.fooddiary.domain.repository.auth.IAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

//@HiltViewModel
//class CalorieGoalViewModel @Inject constructor(
//    private val authRepository: IAuthRepository
//) : ViewModel() {
//    private val calorieGoalRepository = CalorieGoalRepository()
//
//    private val _calorieGoal = MutableStateFlow<CalorieGoal?>(null)
//    val calorieGoal: StateFlow<CalorieGoal?> = _calorieGoal.asStateFlow()
//
//    private val _isLoading = MutableStateFlow(false)
//    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
//
//    private val _errorMessage = MutableStateFlow<String?>(null)
//    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
//
////    var currentUserId: String? = null
//    var currentUserId: String? = authRepository.getCurrentUser()?.uid
//
//    init {
//        loadCalorieGoal()
//    }
//
////    fun setUserId(userId: String) {
////        if (currentUserId != userId) {
////            currentUserId = userId
////            loadCalorieGoal()
////        }
////    }
//
//    fun loadCalorieGoal() {
//        currentUserId?.let { userId ->
//            viewModelScope.launch {
//                _isLoading.value = true
//                try {
//                    val goal = calorieGoalRepository.getCalorieGoal(userId)
//                    _calorieGoal.value = goal
//                } catch (e: Exception) {
//                    _errorMessage.value = "Ошибка загрузки целей: ${e.message}"
//                } finally {
//                    _isLoading.value = false
//                }
//            }
//        }
//    }
//
//    fun saveCalorieGoal(goal: CalorieGoal) {
//        currentUserId?.let { userId ->
//            viewModelScope.launch {
//                _isLoading.value = true
//                try {
//                    val goalWithUserId = goal.copy(userId = userId)
//                    calorieGoalRepository.saveCalorieGoal(goalWithUserId)
//                    _calorieGoal.value = goalWithUserId
//                } catch (e: Exception) {
//                    _errorMessage.value = "Ошибка сохранения целей: ${e.message}"
//                } finally {
//                    _isLoading.value = false
//                }
//            }
//        }
//    }
//
//    fun getDefaultGoal(dailyCalories: Int): CalorieGoal {
//        return CalorieGoal(
//            dailyCalories = dailyCalories,
//            proteinPercentage = 30,
//            fatPercentage = 30,
//            carbsPercentage = 40
//        )
//    }
//
//    fun clearError() {
//        _errorMessage.value = null
//    }
//}
@HiltViewModel
class CalorieGoalViewModel @Inject constructor(
    private val authRepository: IAuthRepository
) : ViewModel() {
    private val calorieGoalRepository = CalorieGoalRepository()

    private val _calorieGoal = MutableStateFlow<CalorieGoal?>(null)
    val calorieGoal: StateFlow<CalorieGoal?> = _calorieGoal.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    var currentUserId: String? = authRepository.getCurrentUser()?.uid

    init {
        loadCalorieGoal()
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

    // Утилита пересчёта: по любым заполненным полям вычисляет остальные
    fun recalculateGoal(
        dailyCalories: Int,
        weightKg: Double, // вес пользователя для режима г/кг
        proteinMode: String, fatMode: String, carbsMode: String,
        proteinValue: String, fatValue: String, carbsValue: String
    ): CalorieGoal {
        val pVal = proteinValue.toDoubleOrNull() ?: 0.0
        val fVal = fatValue.toDoubleOrNull() ?: 0.0
        val cVal = carbsValue.toDoubleOrNull() ?: 0.0

        var proteinG = 0
        var fatG = 0
        var carbsG = 0
        var proteinGPerKg = 0.0
        var fatGPerKg = 0.0
        var carbsGPerKg = 0.0
        var proteinPct = 0
        var fatPct = 0
        var carbsPct = 0

        // Белки
        when (proteinMode) {
            "grams" -> {
                proteinG = pVal.toInt()
                proteinPct = if (dailyCalories > 0) ((proteinG * 4.0) / dailyCalories * 100).toInt() else 0
                proteinGPerKg = if (weightKg > 0) proteinG / weightKg else 0.0
            }
            "grams_per_kg" -> {
                proteinGPerKg = pVal
                proteinG = if (weightKg > 0) (pVal * weightKg).toInt() else 0
                proteinPct = if (dailyCalories > 0) ((proteinG * 4.0) / dailyCalories * 100).toInt() else 0
            }
            else -> { // percent
                proteinPct = pVal.toInt()
                proteinG = if (dailyCalories > 0) ((dailyCalories * proteinPct / 100.0) / 4).toInt() else 0
                proteinGPerKg = if (weightKg > 0) proteinG / weightKg else 0.0
            }
        }

        // Жиры
        when (fatMode) {
            "grams" -> {
                fatG = fVal.toInt()
                fatPct = if (dailyCalories > 0) ((fatG * 9.0) / dailyCalories * 100).toInt() else 0
                fatGPerKg = if (weightKg > 0) fatG / weightKg else 0.0
            }
            "grams_per_kg" -> {
                fatGPerKg = fVal
                fatG = if (weightKg > 0) (fVal * weightKg).toInt() else 0
                fatPct = if (dailyCalories > 0) ((fatG * 9.0) / dailyCalories * 100).toInt() else 0
            }
            else -> { // percent
                fatPct = fVal.toInt()
                fatG = if (dailyCalories > 0) ((dailyCalories * fatPct / 100.0) / 9).toInt() else 0
                fatGPerKg = if (weightKg > 0) fatG / weightKg else 0.0
            }
        }

        // Углеводы
        when (carbsMode) {
            "grams" -> {
                carbsG = cVal.toInt()
                carbsPct = if (dailyCalories > 0) ((carbsG * 4.0) / dailyCalories * 100).toInt() else 0
                carbsGPerKg = if (weightKg > 0) carbsG / weightKg else 0.0
            }
            "grams_per_kg" -> {
                carbsGPerKg = cVal
                carbsG = if (weightKg > 0) (cVal * weightKg).toInt() else 0
                carbsPct = if (dailyCalories > 0) ((carbsG * 4.0) / dailyCalories * 100).toInt() else 0
            }
            else -> { // percent
                carbsPct = cVal.toInt()
                carbsG = if (dailyCalories > 0) ((dailyCalories * carbsPct / 100.0) / 4).toInt() else 0
                carbsGPerKg = if (weightKg > 0) carbsG / weightKg else 0.0
            }
        }

        return CalorieGoal(
            dailyCalories = dailyCalories,
            proteinMode = proteinMode, fatMode = fatMode, carbsMode = carbsMode,
            proteinPercent = proteinPct, fatPercent = fatPct, carbsPercent = carbsPct,
            proteinGrams = proteinG, fatGrams = fatG, carbsGrams = carbsG,
            proteinGramsPerKg = proteinGPerKg, fatGramsPerKg = fatGPerKg, carbsGramsPerKg = carbsGPerKg
        )
    }
}