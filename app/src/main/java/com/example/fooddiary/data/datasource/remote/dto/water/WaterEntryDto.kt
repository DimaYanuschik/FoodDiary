package com.example.fooddiary.data.datasource.remote.dto.water

import com.google.firebase.firestore.PropertyName
import java.util.Date

data class WaterEntryDto(
    @PropertyName("amountMl") val amountMl: Int = 0,
    @PropertyName("date") val date: Date = Date(),
    @PropertyName("userId") val userId: String = ""
)