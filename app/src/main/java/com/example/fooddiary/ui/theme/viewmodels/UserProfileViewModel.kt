package com.example.fooddiary.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fooddiary.data.models.ActivityLevel
import com.example.fooddiary.data.models.Gender
import com.example.fooddiary.data.models.Goal
import com.example.fooddiary.data.models.UserProfile
import com.example.fooddiary.data.repository.UserProfileRepository
import com.example.fooddiary.data.services.CalorieCalculator
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
        currentUserId?.let { userId ->
            viewModelScope.launch {
                _isLoading.value = true
                _errorMessage.value = null
                _saveSuccess.value = false

                try {
                    // Создаем профиль с текущим userId
                    val profileToSave = if (profile.id.isNotEmpty() && _userProfile.value?.id == profile.id) {
                        // Если это обновление существующего профиля
                        profile.copy(
                            userId = userId,
                            updatedAt = Date()
                        )
                    } else {
                        // Если это новый профиль, сохраняем ID существующего или создаем новый
                        val existingId = _userProfile.value?.id ?: ""
                        profile.copy(
                            id = existingId,
                            userId = userId,
                            createdAt = _userProfile.value?.createdAt ?: Date(),
                            updatedAt = Date()
                        )
                    }

                    val savedId = userProfileRepository.saveUserProfile(profileToSave)

                    // Загружаем обновленный профиль
                    val updatedProfile = userProfileRepository.getUserProfileById(savedId)
                    _userProfile.value = updatedProfile

                    // Пересчитываем калории
                    updatedProfile?.let {
                        _calculatedCalories.value = CalorieCalculator.calculateTargetCalories(it)
                    }

                    _saveSuccess.value = true

                } catch (e: Exception) {
                    _errorMessage.value = "Ошибка сохранения профиля: ${e.message}"
                } finally {
                    _isLoading.value = false
                }
            }
        } ?: run {
            _errorMessage.value = "Пользователь не авторизован"
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