package dev.devlopment.chater.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.devlopment.chater.AIChat.AiChatData
import dev.devlopment.chater.AIChat.AiChatbotdata
import dev.devlopment.chater.AIChat.ChatState
import dev.devlopment.chater.AIChat.ChatUiEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class ChatViewModel : ViewModel() {

    private val _chatState = MutableStateFlow(ChatState())
    val chatState = _chatState.asStateFlow()

    fun onEvent(event: ChatUiEvent) {
        when (event) {
            is ChatUiEvent.SendPrompt -> {
                if (event.prompt.isNotEmpty()) {
                    addPrompt(event.prompt)
                }
            }

            is ChatUiEvent.UpdatePrompt -> {
                _chatState.update {
                    it.copy(prompt = event.newPrompt)
                }
            }
        }
    }

    private fun addPrompt(prompt: String) {
        _chatState.update {
            it.copy(
                chatList = it.chatList.toMutableList().apply {
                    add(0, AiChatbotdata(prompt, true))
                },
                prompt = "",
            )
        }
    }

    private fun getResponse(prompt: String) {
        viewModelScope.launch {
            val chat = AiChatData.getResponse(prompt)
            _chatState.update {
                it.copy(
                    chatList = it.chatList.toMutableList().apply {
                        add(0, chat)
                    }
                )
            }
        }
    }

}


















