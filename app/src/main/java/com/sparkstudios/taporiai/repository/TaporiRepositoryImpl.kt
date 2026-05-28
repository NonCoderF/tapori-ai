package com.sparkstudios.taporiai.repository

import com.sparkstudios.taporiai.network.ApiService
import com.sparkstudios.taporiai.network.ChatDownloadRequest
import com.sparkstudios.taporiai.network.ChatDownloadResponse
import com.sparkstudios.taporiai.network.ChatRequest
import com.sparkstudios.taporiai.network.ChatResponse
import com.sparkstudios.taporiai.network.CreditRequest
import com.sparkstudios.taporiai.network.CreditResponse
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaporiRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : TaporiRepository {

    override suspend fun sendMessage(
        idToken: String,
        chatId: String,
        prompt: String,
        systemMessage: String,
        maxContextMessages: Int
    ): Response<ChatResponse> {
        return apiService.sendMessage(
            ChatRequest(
                idToken = idToken,
                chat_id = chatId,
                prompt = prompt,
                system_message = systemMessage,
                max_context_messages = maxContextMessages
            )
        )
    }

    override suspend fun addCredit(idToken: String, creditsToAdd: Int): Response<CreditResponse> {
        return apiService.addCredit(
            CreditRequest(
                idToken = idToken,
                creditsToAdd = creditsToAdd
            )
        )
    }

    override suspend fun downloadChat(idToken: String): Response<ChatDownloadResponse> {
        return apiService.downloadChat(
            ChatDownloadRequest(
                idToken = idToken
            )
        )
    }
}
