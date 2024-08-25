package dev.devlopment.chater.Navigations

sealed class Screen(val route:String){
    object LoginScreen: Screen("login screen")
    object SignupScreen: Screen("signupscreen")
    object ChatRoomsScreen: Screen("chatroomscreen")
    object ChatScreen: Screen("chatscreen")
    object AichatScreen: Screen("Aichatscreen")

    object JoinRequestsScreen: Screen("joinrequestscreen")
    object AiItem:Screen("AiItem")
}