package com.sparkstudios.taporiai.presentation.chat

import android.app.Application
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.sparkstudios.taporiai.network.ErrorResponse
import com.sparkstudios.taporiai.repository.TaporiRepository
import com.sparkstudios.taporiai.screens.ChatMessage
import com.sparkstudios.taporiai.utils.Prefs
import com.sparkstudios.taporiai.utils.generateRandomString
import com.sparkstudios.taporiai.utils.logout
import com.sparkstudios.taporiai.utils.refreshToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val inputText: TextFieldValue = TextFieldValue(""),
    val isSending: Boolean = false,
    val isLoading: Boolean = false,
    val loadingError: String? = null,
    val loadingErrorResponseCode: Int? = null,
    val chatErrorResponseCode: Int? = null,
    val userName: String = "",
    val navigateToSignIn: Boolean = false
)

class ChatViewModel @Inject constructor(
    private val repository: TaporiRepository,
    private val application: Application
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        _uiState.update { it.copy(userName = Prefs.getUserName(application) ?: "") }
        loadChats()
    }

    fun onInputTextChanged(newValue: TextFieldValue) {
        _uiState.update { it.copy(inputText = newValue) }
    }

    fun clearChatErrorResponseCode() {
        _uiState.update { it.copy(chatErrorResponseCode = null) }
    }

    fun loadChats() {
        _uiState.update { it.copy(isLoading = true, loadingError = null, loadingErrorResponseCode = null) }
        refreshToken(
            context = application,
            onRefreshed = {
                viewModelScope.launch {
                    try {
                        val userIdToken = Prefs.getUserIdToken(application) ?: ""
                        val response = repository.downloadChat(userIdToken)
                        if (response.isSuccessful) {
                            response.body()?.chats?.let { msgs ->
                                val mappedMessages = msgs.mapIndexed { index, msg ->
                                    ChatMessage(
                                        id = index,
                                        text = msg.content,
                                        isUser = msg.role == "user"
                                    )
                                }

                                if (msgs.isNotEmpty()) {
                                    Prefs.saveChatId(application, msgs.last().chat_id)
                                } else {
                                    Prefs.saveChatId(application, generateRandomString())
                                }

                                _uiState.update {
                                    it.copy(
                                        messages = mappedMessages,
                                        loadingError = null,
                                        isLoading = false
                                    )
                                }
                            }
                        } else {
                            val code = response.code()
                            val errorMsg = if (code == 401) {
                                "Arre bhai, token expire ho gaya!"
                            } else {
                                val errorJson = response.errorBody()?.string()
                                errorJson?.let {
                                    try {
                                        Gson().fromJson(it, ErrorResponse::class.java).error
                                    } catch (e: Exception) {
                                        "Arre bhai, server ka scene samajh nahi aaya re!"
                                    }
                                } ?: "Arre bhai, server ka scene samajh nahi aaya re!"
                            }
                            _uiState.update {
                                it.copy(
                                    loadingError = errorMsg,
                                    loadingErrorResponseCode = code,
                                    isLoading = false
                                )
                            }
                        }
                    } catch (e: Exception) {
                        _uiState.update {
                            it.copy(
                                loadingError = "Arre bhai, chat pakad nahi paaye… dobara try kar re!",
                                isLoading = false
                            )
                        }
                    }
                }
            },
            onFailure = {
                logoutUser()
            }
        )
    }

    fun sendMessage() {
        sendPrompt(_uiState.value.inputText.text)
    }

    fun sendVoiceMessage(prompt: String) {
        sendPrompt(prompt)
    }

    private fun sendPrompt(prompt: String) {
        if (prompt.isBlank() || _uiState.value.isSending) return

        // Clear input text UI immediately
        _uiState.update { it.copy(inputText = TextFieldValue("")) }

        viewModelScope.launch {
            val currentMessages = _uiState.value.messages
            val userMessage = ChatMessage(
                id = currentMessages.size + 1,
                text = prompt,
                isUser = true
            )

            _uiState.update {
                it.copy(
                    messages = currentMessages + userMessage,
                    isSending = true
                )
            }

            try {
                val userId = Prefs.getUserIdToken(application) ?: ""
                val chatId = Prefs.getChatId(application) ?: ""
                val response = repository.sendMessage(
                    idToken = userId,
                    chatId = chatId,
                    prompt = prompt
                )

                if (response.isSuccessful) {
                    val replyMsg = response.body()?.reply ?: "No response"
                    val assistantMessage = ChatMessage(
                        id = _uiState.value.messages.size + 1,
                        text = replyMsg,
                        isUser = false
                    )
                    _uiState.update {
                        it.copy(
                            messages = it.messages + assistantMessage,
                            isSending = false
                        )
                    }
                } else {
                    val code = response.code()
                    if (code == 402) {
                        val json = JSONObject(response.errorBody()?.string() ?: "{}")
                        val reply = json.optString("reply", "Credit khatam ho gaya re!")
                        val systemMessage = ChatMessage(
                            id = _uiState.value.messages.size + 1,
                            text = reply,
                            isUser = false
                        )
                        _uiState.update {
                            it.copy(
                                messages = it.messages + systemMessage,
                                chatErrorResponseCode = code,
                                isSending = false
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(isSending = false)
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isSending = false)
                }
            }
        }
    }

    fun logoutUser() {
        logout(application)
        _uiState.update { it.copy(navigateToSignIn = true) }
    }
}
