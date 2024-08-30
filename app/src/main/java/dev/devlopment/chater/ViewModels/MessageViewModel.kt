package dev.devlopment.chater.ViewModels


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dev.devlopment.chater.MainAndUtils.Injection
import dev.devlopment.chater.Repository.Message
import dev.devlopment.chater.Repository.MessageRepository
import dev.devlopment.chater.Repository.Result
import dev.devlopment.chater.Repository.RoomRepository
import dev.devlopment.chater.Repository.User
import dev.devlopment.chater.Repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MessageViewModel : ViewModel() {

    private val messageRepository: MessageRepository
    private val userRepository: UserRepository
    private val roomRepository: RoomRepository

    init {
        messageRepository = MessageRepository(Injection.instance())
        userRepository = UserRepository(FirebaseAuth.getInstance(), Injection.instance())
        roomRepository = RoomRepository(Injection.instance())
        loadCurrentUser()
    }

    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> get() = _messages

    private val _roomId = MutableLiveData<String>()
    private val _currentUser = MutableLiveData<User>()
    val currentUser: LiveData<User> get() = _currentUser

    private val _sendMessageResult = MutableLiveData<Result<Unit>?>()
    val sendMessageResult: MutableLiveData<Result<Unit>?> get() = _sendMessageResult

    fun setRoomId(roomId: String) {
        _roomId.value = roomId
        loadMessages()
    }

    fun sendMessage(text: String) {
        val user = _currentUser.value
        val roomId = _roomId.value

        if (user != null && roomId != null) {
            viewModelScope.launch {
                val roomResult = withContext(Dispatchers.IO) {
                    roomRepository.getRoomById(roomId)
                }
                if (roomResult is Result.Success) {
                    val room = roomResult.data
                    //if (user.email == room.creatorId) {
                        val message = Message(
                            senderFirstName = user.firstName,
                            senderId = user.email,
                            text = text
                        )
                        _sendMessageResult.value = withContext(Dispatchers.IO) {
                            messageRepository.sendMessage(roomId, message)
                        }
//                    } else {
//                        _sendMessageResult.value = Result.Error(Exception("Only the room creator can send messages."))
//                    }
                } else {
                    _sendMessageResult.value = Result.Error(Exception("Room not found."))
                }
            }
        }
    }

    fun loadMessages() {
        viewModelScope.launch {
            _roomId.value?.let { roomId ->
                messageRepository.getChatMessages(roomId)
                    .collect { messages ->
                        _messages.value = messages
                    }
            }
        }
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                userRepository.getCurrentUser()
            }
            when (result) {
                is Result.Success -> _currentUser.value = result.data
                is Result.Error -> {
                    // Handle error
                }
            }
        }
    }
}



