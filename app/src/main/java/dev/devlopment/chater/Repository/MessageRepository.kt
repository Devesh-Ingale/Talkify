package dev.devlopment.chater.Repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await


class MessageRepository(private val firestore: FirebaseFirestore) {

    suspend fun sendMessage(roomId: String, message: Message): Result<Unit> {
        return try {
            val roomDoc = firestore.collection("rooms").document(roomId).get().await()
            val room = roomDoc.toObject(Room::class.java) ?: return Result.Error(Exception("Room not found"))

//            // Check if the sender is the creator
//            if (message.senderId != room.creatorId) {
//                return Result.Error(Exception("Only the room creator can send messages."))
//            }

            firestore.collection("rooms").document(roomId)
                .collection("messages").add(message).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    fun getChatMessages(roomId: String): Flow<List<Message>> = callbackFlow {
        val subscription = firestore.collection("rooms").document(roomId)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { querySnapshot, _ ->
                querySnapshot?.let {
                    trySend(it.documents.map { doc ->
                        doc.toObject(Message::class.java)!!.copy()
                    }).isSuccess
                }
            }

        awaitClose { subscription.remove() }
    }
}


