package com.example.fooddiary.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fooddiary.data_old.models.ActivityLevel
import com.example.fooddiary.data_old.models.Gender
import com.example.fooddiary.data_old.models.Goal
import com.example.fooddiary.data_old.models.UserProfile
import com.example.fooddiary.data_old.repository.UserProfileRepository
import com.example.fooddiary.data_old.services.CalorieCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

class UserProfileViewModel : ViewModel() {
    private val userProfileRepository = UserProfileRepository()

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _calculatedCalories = MutableStateFlow(0)
    val calculatedCalories: StateFlow<Int> = _calculatedCalories.asStateFlow()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess.asStateFlow()

    var currentUserId: String? = null
        private set

    fun setUserId(userId: String) {
        if (currentUserId != userId) {
            currentUserId = userId
            loadUserProfile()
        }
    }

    fun loadUserProfile() {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                _isLoading.value = true
                _errorMessage.value = null
                try {
                    val profile = userProfileRepository.getUserProfile(userId)
                    _userProfile.value = profile

                    // Рассчитываем калории если профиль существует
                    profile?.let {
                        _calculatedCalories.value = CalorieCalculator.calculateTargetCalories(it)
                    }
                } catch (e: Exception) {
                    _errorMessage.value = "Ошибка загрузки профиля: ${e.message}"
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }

    fun saveUserProfile(profile: UserProfile) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _saveSuccess.value = false

            try {
                val userId = currentUserId
                if (userId == null) {
                    _errorMessage.value = "Пользователь не авторизован"
                    return@launch
                }

                println("DEBUG: Saving profile for userId: $userId")

                // Создаем профиль с правильным userId
                val profileToSave = profile.copy(
                    userId = userId,
                    updatedAt = Date()
                )

                println("DEBUG: Profile to save: $profileToSave")

                // Сохраняем в Firebase
                val savedId = userProfileRepository.saveUserProfile(profileToSave)
                println("DEBUG: Saved with ID: $savedId")

                // Загружаем обновленный профиль
                val updatedProfile = userProfileRepository.getUserProfileById(savedId)
                _userProfile.value = updatedProfile
                println("DEBUG: Loaded profile after save: $updatedProfile")

                // Пересчитываем калории
                updatedProfile?.let {
                    _calculatedCalories.value = CalorieCalculator.calculateTargetCalories(it)
                }

                _saveSuccess.value = true
                println("DEBUG: Save success!")

            } catch (e: Exception) {
                println("DEBUG: Error: ${e.message}")
                _errorMessage.value = "Ошибка сохранения профиля: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun calculateCaloriesForCurrentState(
        name: String,
        gender: Gender,
        birthDate: Date,
        weight: Double,
        height: Int,
        activityLevel: ActivityLevel,
        goal: Goal
    ): Int {
        val tempProfile = UserProfile(
            name = name,
            gender = gender,
            birthDate = birthDate,
            weight = weight,
            height = height,
            activityLevel = activityLevel,
            goal = goal
        )
        return CalorieCalculator.calculateTargetCalories(tempProfile)
    }

    fun calculateCaloriesForProfile(profile: UserProfile): Int {
        return CalorieCalculator.calculateTargetCalories(profile)
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun resetSaveSuccess() {
        _saveSuccess.value = false
    }
}