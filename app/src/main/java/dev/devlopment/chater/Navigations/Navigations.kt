package dev.devlopment.chater.Navigations

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
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
                OnNavigateToSignUp =  { navController.navigate(Screen.SignupScreen.route) }
            ) {
                navController.navigate(Screen.ChatRoomsScreen.route)
            }
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

        // Add destination for AiChatScreen
        composable(Screen.AichatScreen.route){
            AiChatScreen(paddingValues = PaddingValues(0.dp))
        }

        // Add destination for AiItem
        composable(Screen.AiItem.route) {
            // No action needed here, as it's just a placeholder for navigation
        }

        
    }
}