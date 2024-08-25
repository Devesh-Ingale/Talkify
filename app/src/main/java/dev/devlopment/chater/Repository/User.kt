package dev.devlopment.chater.Repository


data class User(
    val userId: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val createdRoom: String? = null,
    val joinedRooms: List<String> = listOf(),
    val userJoinLink: String = ""
)









