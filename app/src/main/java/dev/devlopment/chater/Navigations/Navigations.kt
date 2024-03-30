package dev.devlopment.Chater.Navigations

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.devlopment.Chater.Screens.ChatRoomListScreen
import dev.devlopment.Chater.Screens.ChatScreen
import dev.devlopment.Chater.Screens.LoginScreen
import dev.devlopment.Chater.Screens.SignUpScreen
import dev.devlopment.Chater.ViewModels.AuthViewModel

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
            ChatRoomListScreen {
                navController.navigate("${Screen.ChatScreen.route}/${it.id}")
            }
        }

        composable("${Screen.ChatScreen.route}/{roomId}") {
            val roomId: String = it
                .arguments?.getString("roomId") ?: ""
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ChatScreen(roomId = roomId)
            }
        }
    }
}