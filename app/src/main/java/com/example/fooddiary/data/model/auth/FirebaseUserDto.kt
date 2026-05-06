package com.example.fooddiary.data.model.auth

import com.google.firebase.auth.FirebaseUser

data class FirebaseUserDto(
    val uid: String,
    val email: String?,
    val isEmailVerified: Boolean,
    val displayName: String?,
    val photoUrl: String?
) {
    // Конвертация из FirebaseUser
    companion object {
        fun fromFirebaseUser(user: FirebaseUser): FirebaseUserDto {
            return FirebaseUserDto(
                uid = user.uid,
                email = user.email,
                isEmailVerified = user.isEmailVerified,
                displayName = user.displayName,
                photoUrl = user.photoUrl?.toString()
            )
        }
    }
}