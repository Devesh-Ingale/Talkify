package dev.devlopment.chater.Repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository(private val auth: FirebaseAuth, private val firestore: FirebaseFirestore) {

    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun signUp(email: String, password: String, firstName: String, lastName: String): Result<Boolean> {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            val user = User(email, firstName, lastName, email)
            saveUserToFirestore(user)
            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    private suspend fun saveUserToFirestore(user: User) {
        firestore.collection("users").document(user.userId).set(user).await()
    }

    suspend fun login(email: String, password: String): Result<Boolean> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.Success(true)
        } catch (e: Exception) {
            Log.e("UserRepository", "Login failed: ${e.message}", e)
            Result.Error(e)
        }
    }

    suspend fun getCurrentUser(): Result<User> {
        return try {
            val email = auth.currentUser?.email
            if (email != null) {
                val userDocument = firestore.collection("users").document(email).get().await()
                val user = userDocument.toObject(User::class.java)
                if (user != null) {
                    Log.d("UserRepository", "User fetched: $email")
                    Result.Success(user)
                } else {
                    Result.Error(Exception("User data not found"))
                }
            } else {
                Result.Error(Exception("User not authenticated"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun searchUsers(query: String): Result<List<User>> = try {
        val querySnapshot = firestore.collection("users")
            .whereGreaterThanOrEqualTo("email", query)
            .whereLessThanOrEqualTo("email", query + "\uf8ff")
            .get().await()
        val users = querySnapshot.documents.map { document ->
            document.toObject(User::class.java)!!
        }
        Result.Success(users)
    } catch (e: Exception) {
        Result.Error(e)
    }

    suspend fun sendMessageToUser(senderId: String, receiverId: String, message: Message): Result<Unit> = try {
        firestore.collection("users").document(receiverId)
            .collection("messages").add(message).await()
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    suspend fun getUserJoinLink(userId: String): Result<String> {
        return try {
            val userDoc = firestore.collection("users").document(userId).get().await()
            val userJoinLink = userDoc.getString("userJoinLink") ?: ""
            Result.Success(userJoinLink)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

}
