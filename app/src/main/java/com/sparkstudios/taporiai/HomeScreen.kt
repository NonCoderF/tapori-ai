package com.sparkstudios.taporiai

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.sparkstudios.taporiai.network.ChatRequest
import com.sparkstudios.taporiai.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val userId = Prefs.getUserId(context)
    val userName = Prefs.getUserName(context)

    var inputText by remember { mutableStateOf("") }
    var responseText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Hello, $userName!", fontSize = 22.sp)
        Text("Your Google ID: $userId", fontSize = 16.sp, modifier = Modifier.padding(bottom = 20.dp))

        // Input box
        OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("Enter text") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        )

        // Submit button for Retrofit call
        Button(onClick = {
            if (inputText.isNotBlank()) {
                isLoading = true
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val chatId = Prefs.getChatId(context) ?: UUID.randomUUID().toString().also {
                            Prefs.saveChatId(context, it)
                        }

                        val userId = Prefs.getUserId(context) ?: ""

                        Log.e("TAG", "userId: $userId , $chatId")

                        val response = RetrofitClient.apiService.sendMessage(
                            ChatRequest(
                                user_id = userId,
                                chat_id = "existing-chat-id-uuid",
                                prompt = inputText,
                                system_message = "You are a Mumbai Tapori assistant. Reply in Mumbai slang hinglish language fully.",
                                max_context_messages = 50
                            )
                        )
                        if (response.isSuccessful) {
                            responseText = response.body()?.reply ?: "No response"
                        } else {
                            responseText = "Error: ${response.code()}"
                        }
                    } catch (e: Exception) {
                        responseText = "Exception: ${e.message}"
                    } finally {
                        isLoading = false
                    }
                }
            } else {
                Toast.makeText(context, "Please enter some text", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("Submit")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Loader
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(8.dp))
        }

        // Response text
        if (responseText.isNotBlank()) {
            Text("Response: $responseText", fontSize = 16.sp, modifier = Modifier.padding(top = 8.dp))
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Logout button
        Button(onClick = {
            GoogleSignIn.getClient(context, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut()
            Prefs.clearUser(context)
            navController.navigate(Screen.SignIn.route) {
                popUpTo(Screen.Home.route) { inclusive = true }
            }
        }) {
            Text("Logout", fontSize = 18.sp)
        }
    }
}
