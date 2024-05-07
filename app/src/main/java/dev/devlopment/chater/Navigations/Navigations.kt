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
import dev.devlopment.chater.ViewModels.RoomViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val loggedInUser = authViewModel.loggedIn.value

    // Navigate based on the authentication result
    LaunchedEffect(loggedInUser) {
        loggedInUser?.let {
            if (it) {
                navController.navigate(Screen.ChatRoomsScreen.route)
            } else {
                navController.navigate(Screen.LoginScreen.route)
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
                onNavigateToLogin = { navController.navigate(Screen.LoginScreen.route) }
            )
        }
        composable(Screen.LoginScreen.route) {
            LoginScreen(
                authViewModel = authViewModel,
                OnNavigateToSignUp = { navController.navigate(Screen.SignupScreen.route) },
                onLoginSuccess = {
                    navController.navigate(Screen.ChatRoomsScreen.route)
                }
            )
        }
        composable(Screen.ChatRoomsScreen.route) {
            ChatRoomListScreen(
                roomViewModel = RoomViewModel(),
                onJoinClicked = {
                    navController.navigate("${Screen.ChatScreen.route}/${it.id}")
                },
                onAiClicked = {
                    navController.navigate(Screen.AichatScreen.route)
                }
            )
        }

        composable("${Screen.ChatScreen.route}/{roomId}") {
            val roomId: String = it.arguments?.getString("roomId") ?: ""
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ChatScreen(roomId = roomId)
            }
        }

        composable(Screen.AichatScreen.route) {
            AiChatScreen(paddingValues = PaddingValues(0.dp))
        }

        // Placeholder composable for future screens
        composable(Screen.AiItem.route) {
            // No action needed here, as it's just a placeholder for navigation
        }
    }
}
