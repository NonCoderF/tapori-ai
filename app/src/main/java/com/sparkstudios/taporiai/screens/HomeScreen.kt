package com.sparkstudios.taporiai.screens

import android.util.Log
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sparkstudios.taporiai.Screen
//import com.razorpay.Checkout
import com.sparkstudios.taporiai.network.ChatRequest
import com.sparkstudios.taporiai.network.CreditRequest
import com.sparkstudios.taporiai.network.RetrofitClient
import com.sparkstudios.taporiai.utils.Prefs
import com.sparkstudios.taporiai.utils.logout
import com.sparkstudios.taporiai.utils.refreshToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.UUID

@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
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
        Text("Hello, $userName!", fontSize = 22.sp, modifier = Modifier.padding(bottom = 20.dp))

        // Input box
        OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("Enter text") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )

        // Submit button for Retrofit call
        Button(onClick = {
            if (inputText.isNotBlank()) {
                isLoading = true
                refreshToken(
                    context = context,
                    onRefreshed = {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                Prefs.getChatId(context) ?: UUID.randomUUID().toString().also {
                                    Prefs.saveChatId(context, it)
                                }
                                val userId = Prefs.getUserIdToken(context) ?: ""

                                val response = RetrofitClient.apiService.sendMessage(
                                    ChatRequest(
                                        idToken = userId,
                                        chat_id = "existing-chat-id-uuid",
                                        prompt = inputText,
                                        system_message = "You are a Mumbai Tapori assistant. Reply in Mumbai slang hinglish language fully.",
                                        max_context_messages = 50
                                    )
                                )
                                if (response.isSuccessful) {
                                    responseText = response.body()?.reply ?: "No response"
                                } else {
                                    responseText = if (response.code() == 402) {
                                        val json =
                                            JSONObject(response.errorBody()?.string() ?: "{}")
                                        val reply =
                                            json.optString("reply", "Credit khatam ho gaya re!")
                                        reply
                                    } else {
                                        "Error: ${response.code()}"
                                    }
                                }
                            } catch (e: Exception) {
                                responseText = "Exception: ${e.message}"
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
            } else {
                Toast.makeText(context, "Please enter some text", LENGTH_SHORT).show()
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
            Text(
                "Response: $responseText",
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(onClick = {
            logout(context)
            navController.navigate(Screen.SignIn.route) {
                popUpTo(Screen.Home.route) { inclusive = true }
            }
        }) {
            Text("Logout", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(30.dp))


//        Button(onClick = {
//            isLoading = true
//            CoroutineScope(Dispatchers.IO).launch {
//                refreshToken(
//                    context = context,
//                    onRefreshed = {
//                        CoroutineScope(Dispatchers.IO).launch {
//                            try {
//                                val response = RetrofitClient.apiService.addCredit(
//                                    CreditRequest(
//                                        idToken = Prefs.getUserIdToken(context) ?: "",
//                                        creditsToAdd = 10
//                                    )
//                                )
//
//                                if (response.isSuccessful) {
//                                    CoroutineScope(Dispatchers.Main).launch {
//                                        Toast.makeText(
//                                            context,
//                                            response.body()?.message,
//                                            LENGTH_SHORT
//                                        ).show()
//                                    }
//                                }
//
//
//                            } catch (e: Exception) {
//                                Log.e("Exception", e.toString())
//                            } finally {
//                                isLoading = false
//                            }
//                        }
//                    },
//                    onFailure = {
//                        logout(context)
//                        navController.navigate(Screen.SignIn.route) {
//                            popUpTo(Screen.Home.route) { inclusive = true }
//                        }
//                    }
//                ).invoke()
//            }
//        }) {
//            Text("Add Credits", fontSize = 18.sp)
//        }

//        PayWithMyUpis()

//        Button(
//            onClick = {
//                CoroutineScope(Dispatchers.IO).launch {
//                    startPayment(context as Activity)
//                }
//            }
//        ){
//            Text("Pay")
//        }


    }
}

//private fun startPayment(activity : Activity) {
//    val checkout = Checkout()
//    checkout.setKeyID("rzp_test_REJGwPsIkwJZRs")
//
//    try {
//        val options = JSONObject()
//        options.put("name", "Spark Studios")
//        options.put("description", "Test Payment")
//        options.put("currency", "INR")
//        options.put("amount", "100") // amount in paise (100 = ₹1)
//
//        val prefill = JSONObject()
//        prefill.put("email", "sallyinfo365@gmail.com")
//        prefill.put("contact", "+917002601418")
//        options.put("prefill", prefill)
//
//        checkout.open(activity, options)
//    } catch (e: Exception) {
//        e.printStackTrace()
//        Toast.makeText(activity, "Error in payment: ${e.message}", Toast.LENGTH_LONG).show()
//    }
//}
