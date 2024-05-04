package dev.devlopment.chater.AIChat

import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object AiChatData {
    val api_key = "AIzaSyBleFTa_Tu_UEwD-uISm629EtiXlY_2Jw4"

    suspend fun getResponse(prompt: String): AiChatbotdata {
        val generativeModel = GenerativeModel(
            modelName = "gemini-pro", apiKey = api_key
        )

        try {
            val response = withContext(Dispatchers.IO) {
                generativeModel.generateContent(prompt)
            }

            return AiChatbotdata(
                prompt = response.text ?: "error",
                isFromUser = false
            )

        } catch (e: Exception) {
            return AiChatbotdata(
                prompt = e.message ?: "error",
                isFromUser = false
            )
        }

    }

}