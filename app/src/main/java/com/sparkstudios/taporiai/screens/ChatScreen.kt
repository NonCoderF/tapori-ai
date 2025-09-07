package com.sparkstudios.taporiai.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.gson.Gson
import com.sparkstudios.tapori.ai.R
import com.sparkstudios.taporiai.Screen
import com.sparkstudios.taporiai.network.ChatDownloadRequest
import com.sparkstudios.taporiai.network.ChatRequest
import com.sparkstudios.taporiai.network.ErrorResponse
import com.sparkstudios.taporiai.network.RetrofitClient
import com.sparkstudios.taporiai.utils.Prefs
import com.sparkstudios.taporiai.utils.generateRandomString
import com.sparkstudios.taporiai.utils.logout
import com.sparkstudios.taporiai.utils.refreshToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.UUID

data class ChatMessage(
    val id: Int,
    val text: String,
    val isUser: Boolean
)

@Composable
fun ChatScreen(navController: NavController, onClose : () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val userName = Prefs.getUserName(context)

    var messages by remember { mutableStateOf(listOf<ChatMessage>()) }
    var inputText by remember { mutableStateOf(TextFieldValue("")) }
    var isSending by remember { mutableStateOf(false) }

    val userIdToken = Prefs.getUserIdToken(context) ?: ""

    var isLoading by remember { mutableStateOf(false) }
    var loadingError: String? by remember { mutableStateOf(null) }
    var chatError: String? by remember { mutableStateOf(null) }
    var loadingErrorResponseCode : Int? by remember { mutableStateOf(null) }
    var showCloseAlertDialog by remember { mutableStateOf(false) }
    var showLogoutAlertDialog by remember { mutableStateOf(false) }

    if (showCloseAlertDialog){
        ChatAlertDialog(
            title = "Oye item?",
            message = "Sach mein nikalna hai kya?",
            onConfirm = {
                showCloseAlertDialog = false
                onClose.invoke()
            },
            onDismiss = {
                showCloseAlertDialog = false
            }
        )
    }

    if (showLogoutAlertDialog){
        ChatAlertDialog(
            title = "Oye item?",
            message = "Sach mein logout kar raha hain kya?",
            onConfirm = {
                showLogoutAlertDialog = false
                logout(context)
                navController.navigate(Screen.SignIn.route) {
                    popUpTo(Screen.Home.route) { inclusive = true }
                }
            },
            onDismiss = {
                showLogoutAlertDialog = false
            }
        )
    }

    BackHandler {
        showCloseAlertDialog = true
    }

    fun loadChats() {
        isLoading = true
        refreshToken(
            context = context,
            onRefreshed = {
                coroutineScope.launch {
                    try {
                        val response = RetrofitClient.apiService.downloadChat(
                            ChatDownloadRequest(idToken = userIdToken)
                        )
                        if (response.isSuccessful) {
                            response.body()?.chats?.let { msgs ->
                                messages = msgs.mapIndexed { index, msg ->
                                    ChatMessage(
                                        id = index,
                                        text = msg.content,
                                        isUser = msg.role == "user"
                                    )
                                }

                                if (msgs.isNotEmpty()){
                                    Prefs.saveChatId(context, msgs.last().chat_id.toString())
                                }else{
                                    Prefs.saveChatId(context, generateRandomString())
                                }
                            }
                            loadingError = null
                        } else {
                            isLoading = false
                            loadingErrorResponseCode = response.code()
                            if (response.code() == 401){
                                loadingError = "Arre bhai, token expire ho gaya!"
                            }else{
                                val errorJson = response.errorBody()?.string()
                                val errorMessage = errorJson?.let {
                                    try {
                                        Gson().fromJson(it, ErrorResponse::class.java).error
                                    } catch (e: Exception) {
                                        "Arre bhai, server ka scene samajh nahi aaya re!"
                                    }
                                } ?: "Arre bhai, server ka scene samajh nahi aaya re!"
                                loadingError = errorMessage
                            }
                        }
                    } catch (e: Exception) {
                        loadingError = "Arre bhai, chat pakad nahi paaye… dobara try kar re!"
                    } finally {
                        isLoading = false
                    }
                }
            },
            onFailure = {
                logout(context)
                navController.navigate(Screen.SignIn.route) {
                    popUpTo(Screen.Home.route) { inclusive = true }
                }
            }
        ).invoke()
    }

    LaunchedEffect(userIdToken) {
        loadChats()
    }

    if (isLoading) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Oye, chat ka jugaad ho raha hai re, thoda time de!",
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }
    } else {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            if (loadingError != null) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = loadingError
                            ?: "Arre bhai, chat pakad nahi paaye… dobara try kar re!",
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )

                    RetryButton(
                        text = if (loadingErrorResponseCode == 401) "Login Again" else "Chal Dobara!"
                    ) {
                        coroutineScope.launch {
                            if (loadingErrorResponseCode == 401){
                                logout(context)
                                navController.navigate(Screen.SignIn.route) {
                                    popUpTo(Screen.Home.route) { inclusive = true }
                                }
                            }
                            loadChats()
                        }
                    }
                }
            } else {
                Scaffold(
                    bottomBar = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White)
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier
                            ) {
                                ChatInputBar(
                                    message = inputText,
                                    onMessageChange = { inputText = it },
                                    isSending = isSending,
                                    onSendClick = {
                                        if (inputText.text.isNotBlank() && !isSending) {
                                            CoroutineScope(Dispatchers.IO).launch {
                                                try {
                                                    val userId = Prefs.getUserIdToken(context) ?: ""

                                                    messages = messages + ChatMessage(
                                                        id = messages.size + 1,
                                                        text = inputText.text,
                                                        isUser = true
                                                    )

                                                    isSending = true

                                                    val response = RetrofitClient.apiService.sendMessage(
                                                        ChatRequest(
                                                            idToken = userId,
                                                            chat_id = Prefs.getChatId(context) ?: "",
                                                            prompt = inputText.text,
                                                            system_message = "You are a Mumbai Tapori assistant. Reply in Mumbai slang hinglish language fully.",
                                                            max_context_messages = 50
                                                        )
                                                    )
                                                    if(response.isSuccessful){
                                                        messages = messages + ChatMessage(
                                                            id = messages.size + 1,
                                                            text = response.body()?.reply ?: "No response",
                                                            isUser = false
                                                        )
                                                        isSending = false
                                                    }else{
                                                        if (response.code() == 402){
                                                            val responseText = if (response.code() == 402) {
                                                                val json =
                                                                    JSONObject(response.errorBody()?.string() ?: "{}")
                                                                val reply =
                                                                    json.optString("reply", "Credit khatam ho gaya re!")
                                                                reply
                                                            } else {
                                                                "Error: ${response.code()}"
                                                            }
                                                            messages = messages + ChatMessage(
                                                                id = messages.size + 1,
                                                                text = responseText,
                                                                isUser = false
                                                            )
                                                        }
                                                        isSending = false
                                                    }
                                                } catch (e: Exception) {
                                                    chatError = e.message
                                                } finally {
                                                    isLoading = false
                                                    inputText = TextFieldValue("")
                                                }
                                            }
                                        }
                                    }
                                )

                            }
                        }
                    },
                    topBar = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White)
                                .padding(
                                    16.dp
                                ),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Kya re item, \n$userName!",
                                    fontSize = 24.sp,
                                    modifier = Modifier.padding(0.dp),
                                    textAlign = TextAlign.Start
                                )

                                IconButton(
                                    onClick = {
                                        showLogoutAlertDialog = true
                                    },
                                    modifier = Modifier
                                        .size(48.dp)
                                ) {
                                    Image(
                                        painter = painterResource(R.drawable.outline_logout_24),
                                        contentDescription = "Logout",
                                    )
                                }
                            }

                        }
                    },
                    modifier = Modifier.padding(
                        top = 32.dp,
                        bottom = 56.dp,
                        start = 16.dp,
                        end = 16.dp
                    ),
                ) { padding ->
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .background(Color.White),
                        reverseLayout = true
                    ) {
                        items(messages.reversed()) { message ->
                            ChatBubble(message)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatInputBar(
    message: TextFieldValue,
    isSending: Boolean = false,
    onMessageChange: (TextFieldValue) -> Unit,
    onSendClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = message,
            onValueChange = onMessageChange,
            placeholder = { Text("Kuch to bol", color = Color.Gray) },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                unfocusedContainerColor = Color.LightGray,
                focusedContainerColor = Color.LightGray
            ),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .weight(1f)
                .height(50.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = Color(0xFF128C7E),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isSending) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                IconButton(
                    onClick = onSendClick,
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = Color(0xFF128C7E),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = Color.White
                    )
                }
            }
        }
    }
}


@Composable
fun ChatBubble(message: ChatMessage) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = if (message.isUser) Color(0xFF1D4760) else Color.LightGray,
                    shape = RoundedCornerShape(
                        topStart = if (message.isUser) 16.dp else 0.dp,
                        topEnd = 16.dp,
                        bottomStart = 16.dp,
                        bottomEnd = if (message.isUser) 0.dp else 16.dp
                    )
                )
                .padding(12.dp)
        ) {
            Text(
                text = message.text,
                color = if (message.isUser) Color.White else Color.Black,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Composable
fun RetryButton(
    text : String,
    onRetry: () -> Unit,
) {
    OutlinedButton(
        onClick = { onRetry() },
        modifier = Modifier
            .padding(horizontal = 16.dp),
    ) {
        Text(
            text = text,
            color = Color.Black,
            fontSize = 16.sp
        )
    }
}

@Composable
fun ChatAlertDialog(
    title: String,
    message: String,
    confirmText: String = "Haan re",
    dismissText: String = "Nahi re",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = { Text(text = message) },
        confirmButton = {
            Button(
                onClick = onConfirm,
                modifier = Modifier.padding(8.dp)
            ) {
                Text(confirmText)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.padding(8.dp)
            ) {
                Text(dismissText)
            }
        }
    )
}
