package com.example.fooddiary.data_old.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AuthRepository @Inject constructor() {
//class AuthRepository {
    private val auth: FirebaseAuth = Firebase.auth

    val currentUser = auth.currentUser
    val isLoggedIn get() = currentUser != null

    suspend fun signInWithEmail(email: String, password: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun signUpWithEmail(email: String, password: String): Boolean {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun signOut() {
        auth.signOut()
    }
}