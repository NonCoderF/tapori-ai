package com.sparkstudios.taporiai.repository

import com.sparkstudios.taporiai.network.ChatDownloadResponse
import com.sparkstudios.taporiai.network.ChatResponse
import com.sparkstudios.taporiai.network.CreditResponse
import retrofit2.Response

interface TaporiRepository {
    suspend fun sendMessage(
        idToken: String,
        chatId: String,
        prompt: String,
        systemMessage: String = "You are a Mumbai Tapori assistant. Reply in Mumbai slang hinglish language fully.",
        maxContextMessages: Int = 50
    ): Response<ChatResponse>

    suspend fun addCredit(idToken: String, creditsToAdd: Int): Response<CreditResponse>

    suspend fun downloadChat(idToken: String): Response<ChatDownloadResponse>
}
