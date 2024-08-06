package dev.devlopment.chater.Repository

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlin.random.Random

class RoomRepository(private val firestore: FirebaseFirestore) {

    suspend fun createRoom(name: String, userId: String): Result<Unit> {
        return try {
            // Fetch the user document
            val userDoc = firestore.collection("users").document(userId).get().await()
            val createdRooms = userDoc.get("createdRooms") as? List<String> ?: emptyList()

            // Check if the user has already created 5 rooms
            if (createdRooms.size >= 5) {
                return Result.Error(Exception("You can only create up to 5 rooms."))
            }

            // Generate a unique 6-digit ID
            val roomId = generateUniqueRoomId()

            // Create the room object
            val room = Room(id = roomId, name = name, creatorId = userId)

            // Add the room to Firestore
            firestore.collection("rooms").document(roomId).set(room).await()

            // Update the user's createdRooms list
            firestore.collection("users").document(userId).update(
                "createdRooms", createdRooms + roomId
            ).await()

            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e("RoomRepository", "Error creating room: ${e.message}", e)
            Result.Error(e)
        }
    }


    private suspend fun generateUniqueRoomId(): String {
        var roomId: String
        do {
            roomId = Random.nextInt(100000, 999999).toString()
            val doc = firestore.collection("rooms").document(roomId).get().await()
        } while (doc.exists())
        return roomId
    }

    suspend fun getRooms(): Result<List<Room>> = try {
        val querySnapshot = firestore.collection("rooms").get().await()
        val rooms = querySnapshot.documents.map { document ->
            document.toObject(Room::class.java)!!.copy(id = document.id)
        }
        Result.Success(rooms)
    } catch (e: Exception) {
        Result.Error(e)
    }

    suspend fun requestToJoinRoom(roomId: String, userId: String): Result<Unit> {
        return try {
            val roomDoc = firestore.collection("rooms").document(roomId).get().await()
            val room = roomDoc.toObject(Room::class.java) ?: throw Exception("Room not found")

            if (!room.pendingRequests.contains(userId)) {
                val updatedRequests = room.pendingRequests.toMutableList()
                updatedRequests.add(userId)
                firestore.collection("rooms").document(roomId).update("pendingRequests", updatedRequests).await()
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error requesting to join room: ${e.message}", e)
            Result.Error(e)
        }
    }

    suspend fun approveJoinRequest(roomId: String, userId: String): Result<Unit> {
        return try {
            val roomDoc = firestore.collection("rooms").document(roomId).get().await()
            val room = roomDoc.toObject(Room::class.java) ?: return Result.Error(Exception("Room not found"))

            if (!room.pendingRequests.contains(userId)) {
                return Result.Error(Exception("Join request not found"))
            }

            val userDoc = firestore.collection("users").document(userId).get().await()
            val user = userDoc.toObject(User::class.java) ?: return Result.Error(Exception("User not found"))

            firestore.collection("rooms").document(roomId).update(
                "members", room.members + userId,
                "pendingRequests", room.pendingRequests - userId
            ).await()

            val joinedRooms = userDoc.get("joinedRooms") as? List<String> ?: emptyList()
            firestore.collection("users").document(userId).update(
                "joinedRooms", joinedRooms + roomId
            ).await()

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }


    suspend fun declineJoinRequest(roomId: String, userId: String): Result<Unit> {
        return try {
            val roomDoc = firestore.collection("rooms").document(roomId).get().await()
            val room = roomDoc.toObject(Room::class.java) ?: return Result.Error(Exception("Room not found"))

            if (!room.pendingRequests.contains(userId)) {
                return Result.Error(Exception("Join request not found"))
            }

            firestore.collection("rooms").document(roomId).update(
                "pendingRequests", room.pendingRequests - userId
            ).await()

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getJoinRequests(roomId: String): Result<List<Pair<String, String>>> {
        return try {
            val roomDoc = firestore.collection("rooms").document(roomId).get().await()
            val room = roomDoc.toObject(Room::class.java) ?: return Result.Error(Exception("Room not found"))

            val userNames = room.pendingRequests.map { userId ->
                val userDoc = firestore.collection("users").document(userId).get().await()
                val user = userDoc.toObject(User::class.java)
                if (user == null) {
                    Log.e("RoomRepository", "User document not found or invalid for userId: $userId")
                } else {
                    Log.d("RoomRepository", "Fetched user: ${user.firstName} ${user.lastName} for userId: $userId")
                }
                Pair(userId, "${user?.firstName ?: ""} ${user?.lastName ?: ""}")
            }

            Result.Success(userNames)
        } catch (e: Exception) {
            Log.e("RoomRepository", "Error fetching join requests: ${e.message}", e)
            Result.Error(e)
        }
    }


    suspend fun getRoomById(roomId: String): Result<Room> {
        return try {
            val roomDoc = firestore.collection("rooms").document(roomId).get().await()
            val room = roomDoc.toObject(Room::class.java)
            if (room != null) {
                Result.Success(room)
            } else {
                Result.Error(Exception("Room not found"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
