package com.example.fooddiary.data.datasource.remote.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.example.fooddiary.data.model.auth.FirebaseUserDto
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthDataSource @Inject constructor(){
    private val auth: FirebaseAuth =  Firebase.auth

    // Flow для отслеживания состояния авторизации
    fun getAuthStateFlow(): Flow<FirebaseUserDto?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            trySend(user?.let{ FirebaseUserDto.fromFirebaseUser(it)})
        }

        auth.addAuthStateListener(listener)

        awaitClose {
            auth.removeAuthStateListener(listener)
        }
    }

    suspend fun signInWithEmail(email: String, password: String): FirebaseUserDto? {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user?.let { FirebaseUserDto.fromFirebaseUser(it) }
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun signUpWithEmail(email: String, password: String): FirebaseUserDto? {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.let { FirebaseUserDto.fromFirebaseUser(it) }
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun signInWithGoogle(idToken: String): FirebaseUserDto {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = auth.signInWithCredential(credential).await()
        return FirebaseUserDto.fromFirebaseUser(result.user!!)
    }

    suspend fun signOut() {
        auth.signOut()
    }

    fun getCurrentUser(): FirebaseUserDto? {
        return auth.currentUser?.let { FirebaseUserDto.fromFirebaseUser(it) }
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }
}


















