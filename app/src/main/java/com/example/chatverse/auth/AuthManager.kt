package com.example.chatverse.auth

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthManager(private val context: Context) {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val credentialManager = CredentialManager.create(context)

    private val webClientId = "846396213702-jm5e2dosdp2si70foiekrr9d1aujjrce.apps.googleusercontent.com"

    suspend fun signInWithGoogle(): AuthResult? {
        try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(context, request)
            val credential = result.credential

            if (credential is GoogleIdTokenCredential) {
                val firebaseCredential = GoogleAuthProvider.getCredential(credential.idToken, null)
                val authResult = auth.signInWithCredential(firebaseCredential).await()
                
                authResult.user?.let { user ->
                    saveUserToFirestore(
                        uid = user.uid,
                        name = user.displayName,
                        email = user.email,
                        photoUrl = user.photoUrl?.toString()
                    )
                }
                return authResult
            }
        } catch (e: Exception) {
            Log.e("AuthManager", "Google Sign-In failed", e)
            throw e
        }
        return null
    }

    suspend fun signUpWithEmail(email: String, password: String, name: String): AuthResult {
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.let { user ->
                saveUserToFirestore(user.uid, name, email, null)
            }
            return result
        } catch (e: Exception) {
            Log.e("AuthManager", "Email Sign-Up failed", e)
            throw e
        }
    }

    suspend fun signInWithEmail(email: String, password: String): AuthResult {
        try {
            return auth.signInWithEmailAndPassword(email, password).await()
        } catch (e: Exception) {
            Log.e("AuthManager", "Email Sign-In failed", e)
            throw e
        }
    }

    private suspend fun saveUserToFirestore(uid: String, name: String?, email: String?, photoUrl: String?) {
        val userMap = hashMapOf(
            "uid" to uid,
            "name" to name,
            "email" to email,
            "photoUrl" to photoUrl,
            "lastLogin" to System.currentTimeMillis()
        )
        
        try {
            db.collection("users").document(uid)
                .set(userMap, com.google.firebase.firestore.SetOptions.merge())
                .await()
        } catch (e: Exception) {
            Log.e("AuthManager", "Firestore sync failed", e)
        }
    }

    fun signOut() {
        auth.signOut()
    }

    fun getCurrentUser() = auth.currentUser
}
