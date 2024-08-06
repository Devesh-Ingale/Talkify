package dev.devlopment.chater.ViewModels


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.devlopment.chater.Repository.Message
import dev.devlopment.chater.Repository.User

class DirectMessageViewModel : ViewModel() {
    private val _users = MutableLiveData<List<User>>(listOf(
        User("Alice", "Wonderland", "alice@example.com"),
        User("Bob", "Builder", "bob@example.com"),
        User("Charlie", "Brown", "charlie@example.com")
    ))
    val users: LiveData<List<User>> = _users

    private val _messages = MutableLiveData<Map<String, List<Message>>>(emptyMap())
    val messages: LiveData<Map<String, List<Message>>> = _messages

    fun sendMessage(sender: User, receiver: User, content: String) {
        val currentMessages = _messages.value.orEmpty().toMutableMap()
        val newMessage = Message(sender.firstName, sender.email, content, System.currentTimeMillis(), sender.email == sender.email)
        currentMessages[receiver.email] = currentMessages[receiver.email].orEmpty() + newMessage
        _messages.value = currentMessages
    }
}
