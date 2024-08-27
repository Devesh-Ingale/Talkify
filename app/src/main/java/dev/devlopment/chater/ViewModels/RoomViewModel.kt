package dev.devlopment.chater.ViewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dev.devlopment.chater.MainAndUtils.Injection
import dev.devlopment.chater.Repository.Result
import dev.devlopment.chater.Repository.Room
import dev.devlopment.chater.Repository.RoomRepository
import dev.devlopment.chater.Repository.User
import dev.devlopment.chater.Repository.UserRepository
import kotlinx.coroutines.launch

class RoomViewModel : ViewModel() {

    private val _rooms = MutableLiveData<List<Room>>()
    val rooms: LiveData<List<Room>> get() = _rooms

    private val roomRepository: RoomRepository = RoomRepository(Injection.instance())
    private val userRepository: UserRepository = UserRepository(FirebaseAuth.getInstance(), Injection.instance())

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> get() = _currentUser

    private val _createRoomResult = MutableLiveData<Result<Unit>>()
    val createRoomResult: LiveData<Result<Unit>> get() = _createRoomResult

    private val _joinRoomResult = MutableLiveData<Result<Unit>>()
    val joinRoomResult: LiveData<Result<Unit>> get() = _joinRoomResult

    private val _approveJoinRequestResult = MutableLiveData<Result<Unit>?>()
    val approveJoinRequestResult: MutableLiveData<Result<Unit>?> get() = _approveJoinRequestResult

    private val _joinRequests = MutableLiveData<List<Pair<String, String>>>()
    val joinRequests: LiveData<List<Pair<String, String>>> get() = _joinRequests

    private var currentUserEmail: String? = null
    private var currentUserId: String? = null

    private val _userJoinLink = MutableLiveData<String>()
    val userJoinLink: LiveData<String> get() = _userJoinLink

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            val result = userRepository.getCurrentUser()
            when (result) {
                is Result.Success -> {
                    _currentUser.value = result.data
                    currentUserEmail = result.data.email
                    currentUserId = result.data.userId
                    loadRooms() // Load rooms only after user data is available
                }
                is Result.Error -> {
                    Log.e("RoomViewModel", "Error loading current user: ${result.exception.message}")
                    // Handle error (e.g., show a message to the user)
                }
            }
        }
    }

    fun createRoom(name: String) {
        _currentUser.value?.let { user ->
            viewModelScope.launch {
                val result = roomRepository.createRoom(name, user.userId)
                _createRoomResult.value = result
                if (result is Result.Success) {
                    loadRooms()
                } else if (result is Result.Error) {
                    Log.e("RoomViewModel", "Error creating room: ${result.exception.message}")
                }
            }
        } ?: Log.e("RoomViewModel", "No current user found!")
    }

    private fun loadRooms() {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                val result = roomRepository.getRooms()
                when (result) {
                    is Result.Success -> {
                        val filteredRooms = result.data.filter { room ->
                            room.members.contains(userId) || room.creatorId == userId
                        }
                        Log.d("RoomViewModel", "Fetched rooms: $filteredRooms")
                        _rooms.value = filteredRooms
                    }
                    is Result.Error -> {
                        Log.e("RoomViewModel", "Error fetching rooms: ${result.exception}")
                        // Handle error
                    }
                }
            }
        } ?: Log.e("RoomViewModel", "Cannot load rooms: Current user is not loaded.")
    }

    fun requestToJoinRoom(roomId: String) {
        _currentUser.value?.let { user ->
            viewModelScope.launch {
                try {
                    roomRepository.requestToJoinRoom(roomId, user.userId)
                    _joinRoomResult.value = Result.Success(Unit)
                } catch (e: Exception) {
                    _joinRoomResult.value = Result.Error(e)
                }
            }
        } ?: Log.e("RoomViewModel", "Cannot request to join room: No current user found!")
    }

    fun approveJoinRequest(roomId: String, userId: String) {
        viewModelScope.launch {
            Log.d("RoomViewModel", "Approving join request for userId: $userId in roomId: $roomId")
            _approveJoinRequestResult.value = roomRepository.approveJoinRequest(roomId, userId)
            loadJoinRequests(roomId) // Reload join requests after approval
        }
    }

    fun declineJoinRequest(roomId: String, userId: String) {
        viewModelScope.launch {
            Log.d("RoomViewModel", "Declining join request for userId: $userId in roomId: $roomId")
            _approveJoinRequestResult.value = roomRepository.declineJoinRequest(roomId, userId)
            loadJoinRequests(roomId) // Reload join requests after decline
        }
    }

    fun loadJoinRequests(roomId: String) {
        viewModelScope.launch {
            Log.d("RoomViewModel", "Loading join requests for roomId: $roomId")
            val result = roomRepository.getJoinRequests(roomId)
            when (result) {
                is Result.Success -> {
                    _joinRequests.value = result.data
                    Log.d("RoomViewModel", "Join requests loaded: ${result.data}")
                }
                is Result.Error -> {
                    Log.e("RoomViewModel", "Error loading join requests: ${result.exception.message}")
                }
            }
        }
    }

    fun isCurrentUserCreatorOfRoom(roomId: String, onResult: (Boolean) -> Unit) {
        _currentUser.value?.userId?.let { userId ->
            viewModelScope.launch {
                val result = roomRepository.getRoomById(roomId)
                when (result) {
                    is Result.Success -> {
                        val room = result.data
                        onResult(room.creatorId == userId)
                    }
                    is Result.Error -> {
                        Log.e("RoomViewModel", "Error fetching room details: ${result.exception.message}")
                        onResult(false)
                    }
                }
            }
        } ?: Log.e("RoomViewModel", "Cannot check room creator: Current user is not loaded.")
    }

    fun loadUserJoinLink() {
        _currentUser.value?.let { user ->
            viewModelScope.launch {
                val result = userRepository.getUserJoinLink(user.userId)
                when (result) {
                    is Result.Success -> {
                        _userJoinLink.value = result.data
                    }
                    is Result.Error -> {
                        Log.e("RoomViewModel", "Error fetching user join link: ${result.exception.message}")
                    }
                }
            }
        } ?: Log.e("RoomViewModel", "Cannot load user join link: No current user found!")
    }
}
