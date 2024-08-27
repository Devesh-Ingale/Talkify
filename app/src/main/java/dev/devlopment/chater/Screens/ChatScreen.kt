package dev.devlopment.chater.Screens

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Yellow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import dev.devlopment.chater.R
import dev.devlopment.chater.Repository.Message
import dev.devlopment.chater.ViewModels.MessageViewModel
import dev.devlopment.chater.ViewModels.RoomViewModel
import dev.devlopment.chater.ui.theme.InterRegular
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChatScreen(
    roomId: String,
    roomName: String,
    navController: NavHostController,
    messageViewModel: MessageViewModel = viewModel(),
    roomViewModel: RoomViewModel = viewModel()
) {
    val messages by messageViewModel.messages.observeAsState(emptyList())
    val currentUser by messageViewModel.currentUser.observeAsState()
    messageViewModel.setRoomId(roomId)
    var message by remember { mutableStateOf("") }

    var showBottomSheet by remember { mutableStateOf(false) }
    var isCreator by remember { mutableStateOf(false) }

    LaunchedEffect(roomId) {
        roomViewModel.isCurrentUserCreatorOfRoom(roomId) { result ->
            isCreator = result
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            TopAppBar(
                title = { Text(text = roomName, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    if (isCreator) {
                        IconButton(onClick = { showBottomSheet = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More Options",
                                tint = Color.White
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(Color.Black)
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 25.dp)
                    .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                    .background(Color.White)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .padding(start = 15.dp, top = 25.dp, end = 15.dp, bottom = 50.dp)
                ) {
                    items(messages) { message ->
                        ChatMessageItem(
                            message = message.copy(isSentByCurrentUser = message.senderId == currentUser?.email)
                        )
                    }
                }
            }
        }

        // Chat input field and send icon
        if (isCreator) {
            CustomTextField(
                text = message,
                onValueChange = { message = it },
                onSendClick = {
                    if (message.isNotEmpty()) {
                        messageViewModel.sendMessage(message.trim())
                        message = ""
                        messageViewModel.loadMessages()
                    }
                },
                onJoinClick = {
                    val joinLink = currentUser?.userJoinLink ?: ""
                    if (isCreator && joinLink.isNotEmpty()) {
                        messageViewModel.sendMessage("join the meeting: $joinLink")
                    }
                },
                modifier = Modifier
                    .align(BottomCenter) // Ensures the text field is at the bottom
                    .padding(16.dp), // Adds padding from the bottom edge
                isCreator = isCreator
            )
        }

        if (showBottomSheet) {
            RoomBottomSheet(
                roomId = roomId,
                onDismiss = { showBottomSheet = false },
                roomViewModel = roomViewModel,
            )
        }
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
                                    Text(text = "IN")
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(onClick = {
                                    Log.d("JoinRequestsDialog", "Deny clicked for $userName")
                                    roomViewModel.declineJoinRequest(roomId, userId)
                                }) {
                                    Text(text = "OUT")
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


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RoomBottomSheet(
    roomId: String,
    onDismiss: () -> Unit,
    roomViewModel: RoomViewModel,
) {
    var showRoomIdDialog by remember { mutableStateOf(false) }
    var showJoinRequestsDialog by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    // State for admin-only mode
    var isAdminOnly by remember { mutableStateOf(false) }

    // Room ID Dialog
    if (showRoomIdDialog) {
        AlertDialog(
            onDismissRequest = { showRoomIdDialog = false },
            title = { Text(text = "Room ID") },
            text = { Text(text = roomId) },
            confirmButton = {
                TextButton(onClick = { showRoomIdDialog = false }) {
                    Text(text = "OK")
                }
            }
        )
    }

    // Join Requests Dialog using the provided JoinRequestsDialog composable
    if (showJoinRequestsDialog) {
        JoinRequestsDialog(
            roomId = roomId,
            roomViewModel = roomViewModel,
            onDismiss = { showJoinRequestsDialog = false }
        )
    }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            TextButton(onClick = { showRoomIdDialog = true }) {
                Text(text = "Show Room ID")
            }
            TextButton(onClick = { showJoinRequestsDialog = true }) {
                Text(text = "Show Join Requests")
            }

            TextButton(onClick = onDismiss) {
                Text(text = "Close")
            }
        }
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    text: String,
    onValueChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onJoinClick: () -> Unit,
    modifier: Modifier = Modifier,
    isCreator: Boolean
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 20.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        shape = RoundedCornerShape(50.dp),
        border = BorderStroke(1.dp, Color.Gray)
    ) {
        TextField(
            value = text,
            onValueChange = { onValueChange(it) },
            placeholder = {
                Text(
                    text = "Type Message....",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = InterRegular,
                        color = Color.Black
                    ),
                    textAlign = TextAlign.Center
                )
            },
            colors = TextFieldDefaults.textFieldColors(
                unfocusedTextColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ),
            leadingIcon = {
                if (isCreator) {
                    CommonIconButton(imageVector = Icons.Default.Add, onJoinClick)
                }
            },
            trailingIcon = {
                CommonIconButtonDrawable(R.drawable.baseline_send_24, onSendClick)
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun CommonIconButton(
    imageVector: ImageVector,onJoinClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .size(33.dp)
            .clip(CircleShape)
            .background(Yellow)
            .clickable { onJoinClick() }, contentAlignment = Center
    ) {
        Icon(
            imageVector = imageVector, contentDescription = "",
            tint = Color.Black,
            modifier = Modifier.size(15.dp)
        )
    }

}

@Composable
fun CommonIconButtonDrawable(
    @DrawableRes icon: Int,
    onSendClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .size(33.dp)
            .clip(CircleShape)
            .background(Yellow)
            .clickable { onSendClick() }, contentAlignment = Center
    ) {
        Icon(
            painter = painterResource(id = icon), contentDescription = "",
            tint = Color.Black,
            modifier = Modifier.size(15.dp)
        )
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChatMessageItem(message: Message) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = if (message.isSentByCurrentUser) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (message.isSentByCurrentUser) Color(0XFFFFE1CC)
                    else Color(0XFFFFF1BF)
                )
                .padding(12.dp)
                .clickable {
                    if (message.text.startsWith("join the meeting: ")) {
                        val url = message.text.removePrefix("join the meeting: ")
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    }
                }
        ) {
            Text(
                text = message.text,
                style = TextStyle(
                    color = Color.Black,
                    fontFamily = InterRegular,
                    fontSize = 15.sp
                ),
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 15.dp),
                textAlign = TextAlign.End
            )
        }
        Text(
            text = message.senderFirstName,
            style = TextStyle(fontSize = 12.sp, color = Color.Gray),
            modifier = Modifier.padding(top = 4.dp)
        )
        Text(
            text = formatTimestamp(message.timestamp),
            style = TextStyle(fontSize = 12.sp, color = Color.Gray),
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun formatTimestamp(timestamp: Long): String {
    val messageDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
    val now = LocalDateTime.now()
    val formatter = if (messageDateTime.toLocalDate() == now.toLocalDate()) {
        DateTimeFormatter.ofPattern("HH:mm")
    } else {
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    }
    return messageDateTime.format(formatter)
}