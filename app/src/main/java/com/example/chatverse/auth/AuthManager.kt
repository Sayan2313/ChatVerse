package com.example.chatverse.auth

import android.content.Context
import android.util.Log
import com.example.chatverse.model.Models
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AuthManager(private val context: Context) {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val credentialManager = androidx.credentials.CredentialManager.create(context)

    private val webClientId = "846396213702-jm5e2dosdp2si70foiekrr9d1aujjrce.apps.googleusercontent.com"

    suspend fun signInWithGoogle(): AuthResult? {
        try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .setAutoSelectEnabled(false)
                .build()

            val request = androidx.credentials.GetCredentialRequest.Builder()
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
        db.collection("users").document(uid).set(userMap, com.google.firebase.firestore.SetOptions.merge()).await()
    }

    fun signOut() {
        auth.signOut()
    }

    fun getCurrentUser() = auth.currentUser

    fun getUserData(): Flow<Models.User?> = callbackFlow {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            trySend(null)
            close()
            return@callbackFlow
        }

        val listener = db.collection("users").document(currentUser.uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val user = snapshot?.toObject(Models.User::class.java)
                trySend(user)
            }
        awaitClose { listener.remove() }
    }

    // Chat Logic
    suspend fun addChatByEmail(email: String): String {
        val currentUser = auth.currentUser ?: throw Exception("Not authenticated")
        
        val userQuery = db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .await()

        if (userQuery.isEmpty) throw Exception("User not found with this email")
        
        val otherUserDoc = userQuery.documents[0]
        val otherUserId = otherUserDoc.id
        val otherUserName = otherUserDoc.getString("name") ?: "Unknown"

        if (otherUserId == currentUser.uid) throw Exception("You cannot chat with yourself")

        val existingChat = db.collection("chats")
            .whereArrayContains("participants", currentUser.uid)
            .get()
            .await()
            .documents
            .firstOrNull { doc ->
                val participants = doc.get("participants") as? List<*>
                participants?.contains(otherUserId) == true
            }

        if (existingChat != null) return existingChat.id

        val chatData = hashMapOf(
            "participants" to listOf(currentUser.uid, otherUserId),
            "lastMessage" to "Hi!",
            "lastTime" to System.currentTimeMillis(),
            "participantDetails" to hashMapOf(
                currentUser.uid to hashMapOf("name" to (currentUser.displayName ?: "Me")),
                otherUserId to hashMapOf("name" to otherUserName)
            )
        )

        val newChatRef = db.collection("chats").add(chatData).await()
        sendMessage(newChatRef.id, "Hi!")
        return newChatRef.id
    }

    fun getChats(): Flow<List<Models.ChatItem>> = callbackFlow {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = db.collection("chats")
            .whereArrayContains("participants", currentUser.uid)
            .orderBy("lastTime", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                val chats = snapshot?.documents?.mapNotNull { doc ->
                    val participants = doc.get("participants") as? List<String> ?: return@mapNotNull null
                    val otherUserId = participants.firstOrNull { it != currentUser.uid } ?: return@mapNotNull null
                    
                    val details = doc.get("participantDetails") as? Map<String, Any>
                    val otherUserDetails = details?.get(otherUserId) as? Map<String, Any>
                    val title = otherUserDetails?.get("name") as? String ?: "Chat"
                    
                    val lastTimeMillis = doc.getLong("lastTime") ?: 0L
                    val lastTimeStr = if (lastTimeMillis == 0L) "" else {
                        SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(lastTimeMillis))
                    }

                    Models.ChatItem(
                        id = doc.id,
                        title = title,
                        lastMessage = doc.getString("lastMessage") ?: "",
                        lastTime = lastTimeStr,
                        otherUserId = otherUserId
                    )
                } ?: emptyList()
                
                trySend(chats)
            }
        
        awaitClose { listener.remove() }
    }

    fun getMessages(chatId: String): Flow<List<Models.Message>> = callbackFlow {
        val currentUser = auth.currentUser ?: return@callbackFlow
        
        val listener = db.collection("chats").document(chatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                val messages = snapshot?.documents?.mapNotNull { doc ->
                    val timestamp = doc.getLong("timestamp") ?: 0L
                    val timeStr = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(timestamp))
                    
                    Models.Message(
                        id = doc.id,
                        text = doc.getString("text") ?: "",
                        time = timeStr,
                        fromMe = doc.getString("senderId") == currentUser.uid,
                        senderId = doc.getString("senderId") ?: "",
                        timestamp = timestamp
                    )
                } ?: emptyList()
                
                trySend(messages)
            }
        
        awaitClose { listener.remove() }
    }

    suspend fun sendMessage(chatId: String, text: String) {
        val currentUser = auth.currentUser ?: return
        val timestamp = System.currentTimeMillis()
        
        val messageData = hashMapOf(
            "text" to text,
            "senderId" to currentUser.uid,
            "timestamp" to timestamp
        )

        db.collection("chats").document(chatId)
            .collection("messages")
            .add(messageData)
            .await()

        db.collection("chats").document(chatId)
            .update(
                "lastMessage", text,
                "lastTime", timestamp
            )
            .await()
    }
}
