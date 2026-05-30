package com.sparkstudios.taporiai.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.sparkstudios.tapori.ai.chatbot.R
import com.sparkstudios.taporiai.Screen
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.imePadding
import com.sparkstudios.taporiai.presentation.chat.ChatViewModel
import java.util.Locale

data class ChatMessage(
    val id: Int,
    val text: String,
    val isUser: Boolean
)

@Composable
fun ChatScreen(
    navController: NavController,
    onClose : () -> Unit,
    onPaymentInvoked : () -> Unit,
    viewModel: ChatViewModel
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var showCloseAlertDialog by remember { mutableStateOf(false) }
    var showLogoutAlertDialog by remember { mutableStateOf(false) }
    var isListening by remember { mutableStateOf(false) }
    var startListeningAfterPermission by remember { mutableStateOf(false) }
    var shouldSpeakNextReply by remember { mutableStateOf(false) }
    var voiceRequestMessageCount by remember { mutableStateOf<Int?>(null) }
    var textToSpeech by remember { mutableStateOf<TextToSpeech?>(null) }
    var isTextToSpeechReady by remember { mutableStateOf(false) }

    val speechRecognizer = remember(context) {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            SpeechRecognizer.createSpeechRecognizer(context)
        } else {
            null
        }
    }

    fun startSpeechRecognizer() {
        val recognizer = speechRecognizer
        if (recognizer == null) {
            Toast.makeText(context, "Voice input available nahi hai", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Bol bhidu...")
        }

        isListening = true
        recognizer.startListening(intent)
    }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            startListeningAfterPermission = true
        } else {
            Toast.makeText(context, "Mic permission chahiye voice ke liye", Toast.LENGTH_SHORT).show()
        }
    }

    DisposableEffect(context) {
        var tts: TextToSpeech? = null
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val languageResult = tts?.setLanguage(Locale("hi", "IN"))
                if (languageResult == TextToSpeech.LANG_MISSING_DATA || languageResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                    tts?.language = Locale.getDefault()
                }
                isTextToSpeechReady = true
            } else {
                isTextToSpeechReady = false
            }
        }
        textToSpeech = tts

        onDispose {
            isTextToSpeechReady = false
            textToSpeech = null
            tts?.stop()
            tts?.shutdown()
        }
    }

    DisposableEffect(speechRecognizer) {
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                isListening = true
            }

            override fun onBeginningOfSpeech() = Unit
            override fun onRmsChanged(rmsdB: Float) = Unit
            override fun onBufferReceived(buffer: ByteArray?) = Unit
            override fun onEndOfSpeech() = Unit
            override fun onPartialResults(partialResults: Bundle?) = Unit
            override fun onEvent(eventType: Int, params: Bundle?) = Unit

            override fun onError(error: Int) {
                isListening = false
                if (error == SpeechRecognizer.ERROR_NO_MATCH || error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
                    Toast.makeText(context, "Kuch sunai nahi diya", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Voice input failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResults(results: Bundle?) {
                isListening = false
                val spokenText = results
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull()
                    ?.trim()

                if (spokenText.isNullOrBlank()) {
                    Toast.makeText(context, "Kuch sunai nahi diya", Toast.LENGTH_SHORT).show()
                } else {
                    voiceRequestMessageCount = uiState.messages.size
                    shouldSpeakNextReply = true
                    viewModel.sendVoiceMessage(spokenText)
                }
            }
        })

        onDispose {
            speechRecognizer?.destroy()
        }
    }

    LaunchedEffect(startListeningAfterPermission) {
        if (startListeningAfterPermission) {
            startListeningAfterPermission = false
            startSpeechRecognizer()
        }
    }

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
                viewModel.logoutUser()
            },
            onDismiss = {
                showLogoutAlertDialog = false
            }
        )
    }

    BackHandler {
        showCloseAlertDialog = true
    }

    LaunchedEffect(uiState.navigateToSignIn) {
        if (uiState.navigateToSignIn) {
            navController.navigate(Screen.SignIn.route) {
                popUpTo(Screen.Home.route) { inclusive = true }
            }
        }
    }

    LaunchedEffect(uiState.messages, uiState.isSending, shouldSpeakNextReply, isTextToSpeechReady) {
        val requestCount = voiceRequestMessageCount
        if (shouldSpeakNextReply && requestCount != null && !uiState.isSending) {
            val latestAssistantReply = uiState.messages
                .drop(requestCount)
                .lastOrNull { !it.isUser }
                ?.text

            if (!latestAssistantReply.isNullOrBlank()) {
                if (isTextToSpeechReady) {
                    textToSpeech?.speak(
                        latestAssistantReply,
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        "tapori_voice_reply_${uiState.messages.size}"
                    )
                }
                shouldSpeakNextReply = false
                voiceRequestMessageCount = null
            }
        }
    }

    if (uiState.isLoading) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Oye, chat ka jugaad ho raha hai re, thoda time de!",
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }
    } else {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            val loadingError = uiState.loadingError
            if (loadingError != null) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = loadingError,
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )

                    RetryButton(
                        text = if (uiState.loadingErrorResponseCode == 401) "Login Again" else "Chal Dobara!"
                    ) {
                        if (uiState.loadingErrorResponseCode == 401){
                            viewModel.logoutUser()
                        } else {
                            viewModel.loadChats()
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
                                .navigationBarsPadding()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier
                            ) {
                                ChatInputBar(
                                    message = uiState.inputText,
                                    onMessageChange = { viewModel.onInputTextChanged(it) },
                                    isSending = uiState.isSending,
                                    isListening = isListening,
                                    onMicClick = {
                                        if (!uiState.isSending && !isListening) {
                                            val hasPermission = ContextCompat.checkSelfPermission(
                                                context,
                                                Manifest.permission.RECORD_AUDIO
                                            ) == PackageManager.PERMISSION_GRANTED

                                            if (hasPermission) {
                                                startSpeechRecognizer()
                                            } else {
                                                micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                            }
                                        }
                                    },
                                    onSendClick = {
                                        if (uiState.inputText.text.isNotBlank() && !uiState.isSending) {
                                            viewModel.sendMessage()
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
                                .statusBarsPadding()
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
                                    "Kya re item, \n${uiState.userName}!",
                                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp),
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
                    modifier = Modifier.fillMaxSize(),
                ) { padding ->
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .background(Color.White),
                        reverseLayout = true
                    ) {
                        if (uiState.chatErrorResponseCode == 402) {
                            item {
                                PayButton(text = "Paisa dalo") {
                                    viewModel.clearChatErrorResponseCode()
                                    onPaymentInvoked.invoke()
                                }
                            }
                        }
                        items(uiState.messages.reversed()) { message ->
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
    isListening: Boolean = false,
    onMessageChange: (TextFieldValue) -> Unit,
    onMicClick: () -> Unit,
    onSendClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(4.dp)
            .imePadding(),
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
                .wrapContentHeight(), // expands naturally with text
            minLines = 1,
            maxLines = Int.MAX_VALUE, // no restriction
        )

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = if (isListening) Color(0xFF128C7E) else Color(0xFF1D4760),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isListening) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                IconButton(
                    onClick = onMicClick,
                    enabled = !isSending,
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = if (isSending) Color.LightGray else Color(0xFF1D4760),
                            shape = CircleShape
                        )
                ) {
                    Image(
                        painter = painterResource(R.drawable.outline_mic_24),
                        contentDescription = "Voice input"
                    )
                }
            }
        }

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
                style = MaterialTheme.typography.bodyLarge,
                color = if (message.isUser) Color.White else Color.Black
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
            style = MaterialTheme.typography.labelLarge.copy(fontSize = 16.sp),
            color = Color.Black
        )
    }
}

@Composable
fun PayButton(
    text : String,
    onPay: () -> Unit,
) {
    OutlinedButton(
        onClick = { onPay() },
        modifier = Modifier
            .padding(horizontal = 16.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(fontSize = 16.sp),
            color = Color.Black
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
        title = { Text(text = title, style = MaterialTheme.typography.titleLarge) },
        text = { Text(text = message, style = MaterialTheme.typography.bodyMedium) },
        confirmButton = {
            Button(
                onClick = onConfirm,
                modifier = Modifier.padding(8.dp)
            ) {
                Text(confirmText, style = MaterialTheme.typography.labelLarge)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.padding(8.dp)
            ) {
                Text(dismissText, style = MaterialTheme.typography.labelLarge)
            }
        }
    )
}
