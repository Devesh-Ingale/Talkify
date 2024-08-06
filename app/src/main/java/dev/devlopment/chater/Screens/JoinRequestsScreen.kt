package dev.devlopment.chater.Screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.devlopment.chater.ViewModels.RoomViewModel

@Composable
fun JoinRequestsScreen(roomId: String, roomViewModel: RoomViewModel = viewModel()) {
    // Trigger loading of join requests when the screen is displayed

    LaunchedEffect(roomId) {
        roomViewModel.loadJoinRequests(roomId)
    }

    // Observe the LiveData for join requests
    val joinRequests by roomViewModel.joinRequests.observeAsState(emptyList())

    Scaffold {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp).padding(it)) {
            Text(text = "Join Requests for Room ID: $roomId")
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(joinRequests) { (userId, userName) ->
                    JoinRequestItem(
                        userName = userName,
                        onApprove = { roomViewModel.approveJoinRequest(roomId, userId) },
                        onDecline = { roomViewModel.declineJoinRequest(roomId, userId) }
                    )
                }
            }
        }
    }
}

@Composable
fun JoinRequestItem(
    userName: String,
    onApprove: () -> Unit,
    onDecline: () -> Unit
) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(text = "User Name: $userName")
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            Button(onClick = onApprove, modifier = Modifier.padding(end = 8.dp)) {
                Text("Approve")
            }
            Button(onClick = onDecline) {
                Text("Decline")
            }
        }
    }
}