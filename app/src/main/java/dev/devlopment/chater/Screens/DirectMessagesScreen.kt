package dev.devlopment.chater.Screens


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.devlopment.chater.Repository.User
import dev.devlopment.chater.ViewModels.DirectMessageViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DirectMessagesScreen(directMessageViewModel: DirectMessageViewModel = viewModel()) {
    val users by directMessageViewModel.users.observeAsState(emptyList())
    val messages by directMessageViewModel.messages.observeAsState(emptyMap())
    var searchText by remember { mutableStateOf("") }
    var selectedUser by remember { mutableStateOf<User?>(null) }
    var messageText by remember { mutableStateOf("") }

    val filteredUsers = users.filter {
        it.firstName.contains(searchText, ignoreCase = true) || it.lastName.contains(searchText, ignoreCase = true)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Search Bar
        TextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("Search Users") },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        )

        // User List
        LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
            items(filteredUsers) { user ->
                UserItem(user = user, onClick = { selectedUser = user })
            }
        }

        // Message List
        selectedUser?.let { user ->
            Column(modifier = Modifier.fillMaxWidth().weight(3f)) {
                LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    items(messages[user.email].orEmpty()) { message ->
                        ChatMessageItem(message = message.copy(isSentByCurrentUser = message.senderId == directMessageViewModel.users.value?.find { it.email == "currentUserId" }?.email))
                    }
                }

                // Send Message
                Row(modifier = Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    BasicTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        textStyle = TextStyle.Default.copy(fontSize = 16.sp),
                        modifier = Modifier.weight(1f).padding(8.dp)
                    )

                    IconButton(
                        onClick = {
                            // Send the message when the icon is clicked
                            if (messageText.isNotEmpty()) {
                                directMessageViewModel.sendMessage(User("currentUserFirstName", "currentUserLastName", "currentUserId"), user, messageText.trim())
                                messageText = ""
                            }
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = "Send")
                    }
                }
            }
        }
    }
}

@Composable
fun UserItem(user: User, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(8.dp)) {
        Text(text = "${user.firstName} ${user.lastName}", style = MaterialTheme.typography.headlineMedium)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun DirectMessagesScreenPreview() {
    DirectMessagesScreen()
}
