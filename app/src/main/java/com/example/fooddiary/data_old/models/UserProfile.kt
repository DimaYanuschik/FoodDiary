package com.example.fooddiary.data_old.models

import java.util.*

data class UserProfile(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val gender: Gender = Gender.MALE,
    val birthDate: Date = Date(),
    val weight: Double = 0.0, // в кг
    val height: Int = 0, // в см
    val activityLevel: ActivityLevel = ActivityLevel.MODERATE,
    val goal: Goal = Goal.MAINTAIN,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),

    val waterGoalMl: Int = 0
) {
    val age: Int
        get() {
            val calendar = Calendar.getInstance()
            val birthCalendar = Calendar.getInstance().apply { time = birthDate }
            var age = calendar.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR)
            if (calendar.get(Calendar.DAY_OF_YEAR) < birthCalendar.get(Calendar.DAY_OF_YEAR)) {
                age--
            }
            return age
        }

    val bmi: Double
        get() = if (height > 0) weight / ((height / 100.0) * (height / 100.0)) else 0.0
}

enum class Gender {
    MALE, FEMALE
}

enum class ActivityLevel(val multiplier: Double, val description: String) {
    SEDENTARY(1.2, "Сидячий образ жизни"),
    LIGHT(1.375, "Легкая активность (1-3 тренировки в неделю)"),
    MODERATE(1.55, "Умеренная активность (3-5 тренировок)"),
    ACTIVE(1.725, "Высокая активность (6-7 тренировок)"),
    VERY_ACTIVE(1.9, "Очень высокая активность (профессиональный спорт)")
}

enum class Goal(val description: String) {
    LOSE_WEIGHT("Похудение"),
    MAINTAIN("Поддержание веса"),
    GAIN_WEIGHT("Набор массы")
}