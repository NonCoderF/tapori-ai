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

interface ApiService {
    @POST("chat")
    suspend fun sendMessage(@Body request: ChatRequest): Response<ChatResponse>

    @POST("add_credits")
    suspend fun addCredit(@Body request: CreditRequest): Response<CreditResponse>
}
