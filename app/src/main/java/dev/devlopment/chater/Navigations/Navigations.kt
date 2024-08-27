package dev.devlopment.chater.Navigations

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.devlopment.chater.Screens.AiChatScreen
import dev.devlopment.chater.Screens.ChatRoomListScreen
import dev.devlopment.chater.Screens.ChatScreen
import dev.devlopment.chater.Screens.LoginScreen
import dev.devlopment.chater.Screens.SignUpScreen
import dev.devlopment.chater.ViewModels.AuthViewModel
import dev.devlopment.chater.ViewModels.MessageViewModel
import dev.devlopment.chater.ViewModels.RoomViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    roomViewModel: RoomViewModel
) {
    val loggedInUser = authViewModel.loggedIn.value
    LaunchedEffect(loggedInUser) {
        loggedInUser?.let {
            if (it) {
                navController.navigate(Screen.ChatRoomsScreen.route)
            } else {
                navController.navigate(Screen.LoginScreen.route) {
                    popUpTo(Screen.ChatRoomsScreen.route) { inclusive = true }
                }
            }
        }
    }


    NavHost(
        navController = navController,
        startDestination = Screen.LoginScreen.route
    ) {
        composable(Screen.SignupScreen.route) {
            SignUpScreen(
                authViewModel = authViewModel,
                onNavigateToLogin = { navController.navigate(Screen.LoginScreen.route) },
                onSignUpSuccess = {navController.navigate(Screen.ChatRoomsScreen.route)}
            )
        }
        composable(Screen.LoginScreen.route) {
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateToSignUp = { navController.navigate(Screen.SignupScreen.route) },
                onLoginSuccess = {
                    navController.navigate(Screen.ChatRoomsScreen.route)
                }
            )
        }
        composable(Screen.ChatRoomsScreen.route) {
            ChatRoomListScreen(
                roomViewModel = roomViewModel,
                authViewModel = authViewModel,
                navController = navController,
                onJoinClicked = { room ->
                    navController.navigate("${Screen.ChatScreen.route}/${room.id}/${room.name}")
                },
                onAiClicked = {
                    navController.navigate(Screen.AichatScreen.route)
                }
            )
        }

        composable("${Screen.ChatScreen.route}/{roomId}/{roomName}") { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("roomId") ?: ""
            val roomName = backStackEntry.arguments?.getString("roomName") ?: ""
            ChatScreen(roomId = roomId, roomName = roomName, roomViewModel = roomViewModel, navController = navController, messageViewModel = MessageViewModel())
        }

        composable(Screen.AichatScreen.route) {
            AiChatScreen(paddingValues = PaddingValues(16.dp))
        }

    }
}
