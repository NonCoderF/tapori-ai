package com.sparkstudios.taporiai.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class ChatRequest(
    val user_id: String,
    val chat_id: String,
    val prompt: String,
    val system_message: String,
    val max_context_messages: Int
)

data class ChatResponse(
    val chat_id: String,
    val reply: String
)

interface ApiService {
    @POST("chat")
    suspend fun sendMessage(@Body request: ChatRequest): Response<ChatResponse>
}
