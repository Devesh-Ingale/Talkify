package dev.devlopment.chater.Repository


data class User(
    val userId: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val createdRooms: List<String> = listOf(), // Updated field
    val joinedRooms: List<String> = listOf(),
    val userJoinLink: String = ""
)










