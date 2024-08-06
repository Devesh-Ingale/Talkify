package dev.devlopment.chater.Screens

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.devlopment.chater.R
import dev.devlopment.chater.Repository.Message
import dev.devlopment.chater.ViewModels.MessageViewModel
import dev.devlopment.chater.ViewModels.RoomViewModel
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChatScreen(
    roomId: String,
    isCreator: Boolean,
    messageViewModel: MessageViewModel = viewModel(),
    roomViewModel: RoomViewModel = viewModel()
) {
    val messages by messageViewModel.messages.observeAsState(emptyList())
    messageViewModel.setRoomId(roomId)
    val text = remember { mutableStateOf("") }
    val showDialog = remember { mutableStateOf(false) }
    val showDialog2 = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Button to display room ID for creator
        if (isCreator) {
            Button(
                onClick = {
                    showDialog.value = true
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = "Show Room ID")
            }
        }

        // Join requests section for creator
        if (isCreator) {
            Button(
                onClick = { showDialog2.value = true },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = "Show Join Requests")
            }
        }

        // Display the chat messages
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(messages) { message ->
                ChatMessageItem(message = message.copy(isSentByCurrentUser = message.senderId == messageViewModel.currentUser.value?.email))
            }
        }

        // Chat input field and send icon
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = text.value,
                onValueChange = { text.value = it },
                textStyle = TextStyle.Default.copy(fontSize = 16.sp),
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            )

            IconButton(
                onClick = {
                    // Send the message when the icon is clicked
                    if (text.value.isNotEmpty()) {
                        messageViewModel.sendMessage(text.value.trim())
                        text.value = ""
                    }
                    messageViewModel.loadMessages()
                }
            ) {
                Icon(imageVector = Icons.Default.Send, contentDescription = "Send")
            }
        }
    }

    // Room ID dialog
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text(text = "Room ID") },
            text = { Text(text = roomId) },
            confirmButton = {
                Button(
                    onClick = { showDialog.value = false }
                ) {
                    Text(text = "OK")
                }
            }
        )
    }

    if (showDialog2.value) {
        JoinRequestsDialog(
            roomId = roomId,
            roomViewModel = roomViewModel,
            onDismiss = { showDialog2.value = false }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun JoinRequestsDialog(
    roomId: String,
    roomViewModel: RoomViewModel,
    onDismiss: () -> Unit
) {
    val joinRequests by roomViewModel.joinRequests.observeAsState(emptyList())

    LaunchedEffect(roomId) {
        roomViewModel.loadJoinRequests(roomId)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Join Requests") },
        text = {
            Column {
                LazyColumn {
                    items(joinRequests) { (userId, userName) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Text(text = userName) // Display the user name
                            Row {
                                Button(onClick = {
                                    Log.d("JoinRequestsDialog", "Approve clicked for $userName")
                                    roomViewModel.approveJoinRequest(roomId, userId)
                                }) {
                                    Text(text = "Accept")
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(onClick = {
                                    Log.d("JoinRequestsDialog", "Deny clicked for $userName")
                                    roomViewModel.declineJoinRequest(roomId, userId)
                                }) {
                                    Text(text = "Deny")
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(text = "Close")
            }
        }
    )
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChatMessageItem(message: Message) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = if (message.isSentByCurrentUser) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .background(
                    if (message.isSentByCurrentUser) colorResource(id = R.color.purple_700) else Color.Gray,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(8.dp)
        ) {
            Text(
                text = message.text,
                color = Color.White,
                style = TextStyle(fontSize = 16.sp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = message.senderFirstName,
            style = TextStyle(
                fontSize = 12.sp,
                color = Color.Gray
            )
        )
        Text(
            text = formatTimestamp(message.timestamp), // Replace with actual timestamp logic
            style = TextStyle(
                fontSize = 12.sp,
                color = Color.Gray
            )
        )
    }
}
@Composable
fun RoomListScreen(roomViewModel: RoomViewModel = viewModel(), currentUserEmail: String) {
    var showDialog by remember { mutableStateOf(false) }
    var roomId by remember { mutableStateOf("") }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Existing code to display the list of rooms

        FloatingActionButton(
            onClick = { showDialog = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Join Room")
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Join Room") },
            text = {
                Column {
                    Text(text = "Enter Room ID")
                    TextField(value = roomId, onValueChange = { roomId = it })
                }
            },
            confirmButton = {
                Button(onClick = {
                    roomViewModel.requestToJoinRoom(roomId)
                    showDialog = false
                }) {
                    Text(text = "Send Request")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text(text = "Cancel")
                }
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun formatTimestamp(timestamp: Long): String {
    val messageDateTime =
        LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
    val now = LocalDateTime.now()
    val formatter = if (messageDateTime.toLocalDate() == now.toLocalDate()) {
        DateTimeFormatter.ofPattern("HH:mm")
    } else {
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    }
    return messageDateTime.format(formatter)
}



@RequiresApi(Build.VERSION_CODES.O)
private fun isSameDay(dateTime1: LocalDateTime, dateTime2: LocalDateTime): Boolean {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return dateTime1.format(formatter) == dateTime2.format(formatter)
}

@RequiresApi(Build.VERSION_CODES.O)
private fun formatTime(dateTime: LocalDateTime): String {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    return formatter.format(dateTime)
}

@RequiresApi(Build.VERSION_CODES.O)
private fun formatDate(dateTime: LocalDateTime): String {
    val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
    return formatter.format(dateTime)
}

@Preview(showBackground = true)
@Composable
fun ChatMessageItemPreview() {}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {}