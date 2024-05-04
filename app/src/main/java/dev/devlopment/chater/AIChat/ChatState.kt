package dev.devlopment.chater.AIChat


data class ChatState (
    val chatList: MutableList<AiChatbotdata> = mutableListOf(),
    val prompt: String = "",
)