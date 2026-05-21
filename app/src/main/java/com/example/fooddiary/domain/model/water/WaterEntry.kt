package com.example.fooddiary.domain.model.water

import java.util.Date

data class WaterEntry (
    val id: String,
    val amountMl: Int,
    val date: Date,
    val userId: String
)