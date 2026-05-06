package com.example.fooddiary.data_old.services

import com.example.fooddiary.data_old.models.Gender
import com.example.fooddiary.data_old.models.Goal
import com.example.fooddiary.data_old.models.UserProfile

object CalorieCalculator {

    /**
     * Рассчитывает базовый метаболизм (BMR) по формуле Харриса-Бенедикта
     */
    fun calculateBMR(profile: UserProfile): Double {
        return when (profile.gender) {
            Gender.MALE -> {
                88.362 + (13.397 * profile.weight) + (4.799 * profile.height) - (5.677 * profile.age)
            }
            Gender.FEMALE -> {
                447.593 + (9.247 * profile.weight) + (3.098 * profile.height) - (4.330 * profile.age)
            }
        }
    }

    /**
     * Рассчитывает дневную потребность в калориях (TDEE)
     */
    fun calculateTDEE(profile: UserProfile): Int {
        val bmr = calculateBMR(profile)
        val tdee = bmr * profile.activityLevel.multiplier
        return tdee.toInt()
    }

    /**
     * Рассчитывает целевые калории с учетом цели
     */
    fun calculateTargetCalories(profile: UserProfile): Int {
        val tdee = calculateTDEE(profile)
        return when (profile.goal) {
            Goal.LOSE_WEIGHT -> (tdee * 0.85).toInt() // Дефицит 15%
            Goal.MAINTAIN -> tdee
            Goal.GAIN_WEIGHT -> (tdee * 1.15).toInt() // Профицит 15%
        }
    }

    /**
     * Рассчитывает макросы в граммах
     */
    fun calculateMacros(calories: Int, proteinPercent: Int, fatPercent: Int, carbsPercent: Int): Map<String, Int> {
        return mapOf(
            "protein" to ((calories * proteinPercent / 100.0) / 4).toInt(),
            "fat" to ((calories * fatPercent / 100.0) / 9).toInt(),
            "carbs" to ((calories * carbsPercent / 100.0) / 4).toInt()
        )
    }

    /**
     * Стандартные процентные соотношения макросов по целям
     */
    fun getDefaultMacroPercentages(goal: Goal): Triple<Int, Int, Int> {
        return when (goal) {
            Goal.LOSE_WEIGHT -> Triple(40, 30, 30) // Высокий белок для сохранения мышц
            Goal.MAINTAIN -> Triple(30, 30, 40) // Сбалансированное соотношение
            Goal.GAIN_WEIGHT -> Triple(25, 25, 50) // Больше углеводов для энергии
        }
    }
}