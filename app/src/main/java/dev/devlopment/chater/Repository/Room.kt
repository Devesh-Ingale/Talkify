package dev.devlopment.chater.Repository

data class Room(
    val id: String = "",
    val name: String = "",
    val creatorId: String = "",
    val members: List<String> = listOf(),
    val pendingRequests: List<String> = listOf()
)

