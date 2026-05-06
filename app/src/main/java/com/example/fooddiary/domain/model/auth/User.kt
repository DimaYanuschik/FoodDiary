package com.example.fooddiary.domain.model.auth

data class User(
    val uid: String,
    val email: String,
    val isEmailVerified: Boolean = false
)