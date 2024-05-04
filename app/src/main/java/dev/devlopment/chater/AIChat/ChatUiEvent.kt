package dev.devlopment.chater.AIChat



sealed class ChatUiEvent {
    data class UpdatePrompt(val newPrompt: String) : ChatUiEvent()
    data class SendPrompt(
        val prompt: String,
    ) : ChatUiEvent()
}
