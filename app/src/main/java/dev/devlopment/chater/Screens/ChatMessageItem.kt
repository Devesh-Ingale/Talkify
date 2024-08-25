package dev.devlopment.chater.Screens


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import dev.devlopment.chater.Navigations.Screen
import dev.devlopment.chater.R
import dev.devlopment.chater.Repository.Room
import dev.devlopment.chater.ViewModels.AuthViewModel
import dev.devlopment.chater.ViewModels.RoomViewModel
import dev.devlopment.chater.ui.theme.focusedTextFieldText
import dev.devlopment.chater.ui.theme.textFieldContainer
import dev.devlopment.chater.ui.theme.unfocusedTextFieldText


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatRoomListScreen(
    roomViewModel: RoomViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel(),
    navController: NavHostController, // Add NavController parameter
    onJoinClicked: (Room) -> Unit,
    onAiClicked: () -> Unit
) {
    val rooms by roomViewModel.rooms.observeAsState(emptyList())
    var showDialog by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var searchText by remember { mutableStateOf("") }
    var joinRoomDialog by remember { mutableStateOf(false) }
    var roomId by remember { mutableStateOf("") }
    val createRoomResult by roomViewModel.createRoomResult.observeAsState()

    createRoomResult?.let { result ->
        when (result) {
            is dev.devlopment.chater.Repository.Result.Success -> {
                // Room creation succeeded, hide dialog and clear input
                showDialog = false
                name = ""
            }
            is dev.devlopment.chater.Repository.Result.Error -> {
                // Handle the error, maybe show a Toast or a Snackbar
            }
        }
    }

    val filteredRooms = rooms.filter { it.name.contains(searchText, ignoreCase = true) }
    val background: Color = if (isSystemInDarkTheme()) Color(0xFF1E293B) else Color(0xFFBFDBFE)
    val uiColor: Color = if (isSystemInDarkTheme()) Color.White else Color.Black

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row {
                IconButton(
                    onClick = {
                        authViewModel.logout()
                        navController.navigate(Screen.LoginScreen.route) // Navigate to login screen on logout
                    },
                    modifier = Modifier
                        .background(background, shape = RoundedCornerShape(50.dp))
                ) {
                    Icon(painter = painterResource(id = R.drawable.logout), contentDescription = "Logout")
                }
                Text(
                    "Chat Rooms",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier
                        .background(background, shape = RoundedCornerShape(5.dp))
                        .fillMaxWidth()
                        .padding(all = 15.dp),
                    color = uiColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .border(
                        BorderStroke(0.5.dp, color = uiColor),
                        shape = RoundedCornerShape(20.dp)
                    ),
                value = searchText,
                onValueChange = { searchText = it },
                label = {
                    Text(
                        text = "Search Rooms",
                        style = MaterialTheme.typography.labelMedium,
                        color = uiColor
                    )
                },
                colors = TextFieldDefaults.textFieldColors(
                    unfocusedLabelColor = MaterialTheme.colorScheme.unfocusedTextFieldText,
                    focusedLabelColor = MaterialTheme.colorScheme.focusedTextFieldText
                ),
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = if (isSystemInDarkTheme()) R.drawable.baseline_search_24_dark else R.drawable.baseline_search_24),
                        contentDescription = "search"
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            AiItem(onAiClicked = onAiClicked)

            LazyColumn {
                items(filteredRooms) { room ->
                    RoomItem(room = room, onJoinClicked = { onJoinClicked(room) })
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { showDialog = true }, modifier = Modifier.fillMaxWidth()) {
                Text("Create Room")
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Create a new room") },
                    text = {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                    },
                    confirmButton = {
                        Button(onClick = {
                            if (name.isNotBlank()) {
                                roomViewModel.createRoom(name)
                            }
                        }) {
                            Text("Add")
                        }

                    },
                    dismissButton = {
                        Button(onClick = { showDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }

        FloatingActionButton(
            onClick = { joinRoomDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Join Room")
        }
    }

    if (joinRoomDialog) {
        AlertDialog(
            onDismissRequest = { joinRoomDialog = false },
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
                    joinRoomDialog = false
                }) {
                    Text(text = "Send Request")
                }
            },
            dismissButton = {
                Button(onClick = { joinRoomDialog = false }) {
                    Text(text = "Cancel")
                }
            }
        )
    }
}


@Composable
fun RoomItem(room: Room, onJoinClicked: (Room) -> Unit) {
    val uiColor: Color = if (isSystemInDarkTheme()) Color.White else Color.Black
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onJoinClicked(room) }
            .background(color = MaterialTheme.colorScheme.textFieldContainer)
            .clip(RoundedCornerShape(20.dp)),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(20.dp))
        Image(
            painter = painterResource(id = R.drawable.baseline_person_24),
            contentDescription = "Room Image",
            modifier = Modifier
                .size(25.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        Spacer(modifier = Modifier.width(20.dp))
        Column {
            Text(
                text = room.name,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.focusedTextFieldText
            )
            Text(
                text = room.name,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Light),
                color = MaterialTheme.colorScheme.focusedTextFieldText
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = room.name,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Light),
            color = MaterialTheme.colorScheme.focusedTextFieldText
        )
        Spacer(modifier = Modifier.width(20.dp))
    }
    Divider(modifier = Modifier.padding(8.dp), thickness = DividerDefaults.Thickness, color = uiColor)
}

@Composable
fun AiItem(onAiClicked: () -> Unit) {
    val uiColor: Color = if (isSystemInDarkTheme()) Color.White else Color.Black
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onAiClicked() }
            .background(color = MaterialTheme.colorScheme.textFieldContainer)
            .clip(RoundedCornerShape(20.dp)),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(20.dp))
        Image(
            painter = painterResource(id = R.drawable.baseline_person_24),
            contentDescription = "Room Image",
            modifier = Modifier
                .size(25.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        Spacer(modifier = Modifier.width(20.dp))
        Column {
            Text(
                text = "Ai Assistant",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.focusedTextFieldText
            )
            Text(
                text = "Chat with Ai Assistant",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Light),
                color = MaterialTheme.colorScheme.focusedTextFieldText
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "Join Room",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Light),
            color = MaterialTheme.colorScheme.focusedTextFieldText
        )
        Spacer(modifier = Modifier.width(20.dp))
    }
    Divider(modifier = Modifier.padding(8.dp), thickness = DividerDefaults.Thickness, color = uiColor)
}

@Preview(showBackground = true)
@Composable
fun RoomItemPreview() {
    RoomItem(room = Room("id.com","Name"),{})
}