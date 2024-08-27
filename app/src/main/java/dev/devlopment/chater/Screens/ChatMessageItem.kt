package dev.devlopment.chater.Screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import dev.devlopment.chater.R
import dev.devlopment.chater.Repository.Room
import dev.devlopment.chater.ViewModels.AuthViewModel
import dev.devlopment.chater.ViewModels.RoomViewModel
import dev.devlopment.chater.ui.theme.InterRegular
import dev.devlopment.chater.ui.theme.InterSemibold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatRoomListScreen(
    roomViewModel: RoomViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel(),
    navController: NavHostController,
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
                showDialog = false
                name = ""
            }
            is dev.devlopment.chater.Repository.Result.Error -> {
                // Handle the error
            }
        }
    }

    val filteredRooms = rooms.filter { it.name.contains(searchText, ignoreCase = true) }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Header(roomViewModel,authViewModel)

            Spacer(modifier = Modifier.size(15.dp))

            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = { Text("Search rooms...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = TextFieldDefaults.colors(Color.White)
            )

            Spacer(modifier = Modifier.size(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                    .background(Color.White)
            ) {
                RoundedCorner(modifier = Modifier
                    .align(TopCenter)
                    .padding(top = 15.dp))

                LazyColumn(modifier = Modifier.padding(bottom = 15.dp, top = 30.dp)) {
                    item {
                        // Add AI chat as the first item in the list
                        UserEachRow(
                            person = Person("ai_chat", "AI Chat", R.drawable.baseline_person_24)
                        ) {
                            onAiClicked()
                        }
                    }
                    items(filteredRooms, key = { it.id }) { room ->
                        UserEachRow(person = Person(room.id, room.name, R.drawable.baseline_person_24)) {
                            onJoinClicked(room)
                        }
                    }

                    // "Create Room" button at the bottom of the list with black background
                    item {
                        Spacer(modifier = Modifier.size(20.dp))
                        Button(
                            onClick = { showDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            colors = ButtonDefaults.buttonColors(Color.Black)
                        ) {
                            Text("Create Room", color = Color.White, modifier = Modifier.align(Alignment.CenterVertically))
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { joinRoomDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = Color.Black
        ) {
            Icon(Icons.Default.Add, contentDescription = "Join Room")
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
}

@Composable
fun Header(
    roomViewModel: RoomViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel() // Pass a lambda to handle logout action
) {
    val currentUser by roomViewModel.currentUser.observeAsState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, top = 60.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center // Align items horizontally centered
    ) {
        IconButton(
            onClick = { authViewModel.logout() },
            modifier = Modifier.align(Alignment.Top)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.logout),
                contentDescription = "Logout",
                tint = Color.White // Set the icon color
            )
        }

        val annotatedString = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    color = Color.White,
                    fontFamily = InterRegular,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.W300
                )
            ) {
                append("Welcome back, ")
            }
            withStyle(
                style = SpanStyle(
                    color = Color.White,
                    fontFamily = InterSemibold,
                    fontSize = 20.sp,
                )
            ) {
                append(currentUser?.lastName ?: "User!")
            }
        }

        Text(text = annotatedString)
    }
}

@Composable
fun RoundedCorner(
    modifier: Modifier
) {
    Box(
        modifier = modifier
            .width(90.dp)
            .height(5.dp)
            .clip(RoundedCornerShape(90.dp))
            .background(
                Color.Gray
            )
    )
}

@Composable
fun UserEachRow(
    person: Person,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .size(70.dp)
            .background(Color.White)
            .noRippleEffect { onClick() }
            .padding(horizontal = 20.dp, vertical = 5.dp),
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    Icon(
                        painter = painterResource(id = person.icon),
                        contentDescription = "",
                        modifier = Modifier.size(30.dp)
                    )
                    Column(
                        modifier = Modifier.padding(start = 15.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = person.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 25.sp,
                        )
                    }
                }
            }

            androidx.compose.material3.Divider(
                modifier = Modifier.padding(top = 5.dp),
                color = Color.LightGray
            )
        }
    }
}

data class Person(val id: String, val name: String, val icon: Int)

@SuppressLint("UnnecessaryComposedModifier", "UnrememberedMutableInteractionSource", "ModifierFactoryUnreferencedReceiver")
fun Modifier.noRippleEffect(onClick: () -> Unit) = composed {
    clickable(
        interactionSource = MutableInteractionSource(),
        indication = null
    ) {
        onClick()
    }
}

