package com.sparkstudios.taporiai.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class ChatRequest(
    val idToken: String,
    val chat_id: String,
    val prompt: String,
    val system_message: String,
    val max_context_messages: Int
)

data class ChatResponse(
    val chat_id: String,
    val reply: String
)

data class CreditRequest(
    val idToken: String,
    val creditsToAdd: Int
)

data class CreditResponse(
    val success: Boolean? = null,
    val message: String? = null,
    val credits: Int? = null
)

data class ChatDownloadRequest(
    val idToken: String,
)

data class ChatDownloadResponse(
    val success: Boolean,
    val chats: List<ChatMessageDto>
)

data class ChatMessageDto(
    val id: String,
    val user_id: String,
    val chat_id: String,
    val role: String,
    val content: String,
    val created_at: String
)

data class ErrorResponse(
    val error: String
)

interface ApiService {
    @POST("chat")
    suspend fun sendMessage(@Body request: ChatRequest): Response<ChatResponse>

    @POST("add_credits")
    suspend fun addCredit(@Body request: CreditRequest): Response<CreditResponse>

    @POST("download")
    suspend fun downloadChat(@Body request: ChatDownloadRequest): Response<ChatDownloadResponse>

}
