package dev.devlopment.chater.AIChat

import com.google.ai.client.generativeai.GenerativeModel

object AiChatData {
    val api_key = "AIzaSyCBNCGUq9q1CS4Y3bgoratlm2m0LDxl_7Y"

    suspend fun getResponse(prompt: String): AiChatbotdata? {
        val generativeModel = GenerativeModel(
            modelName = "gemini-1.5-flash", apiKey = api_key
        )

        try {
            val response = generativeModel.generateContent(prompt)


            return response.text?.let {
                AiChatbotdata(
                    prompt = it,
                    isFromUser = false
                )
            }

        } catch (e: Exception) {
            return e.message?.let {
                AiChatbotdata(
                    prompt = it,
                    isFromUser = false
                )
            }
        }

    }

}