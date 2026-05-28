package com.sparkstudios.taporiai

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import com.sparkstudios.taporiai.presentation.payment.PaymentViewModel
import com.sparkstudios.taporiai.screens.CreditPacksScreen
import com.sparkstudios.taporiai.ui.theme.TaporiAITheme
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import androidx.activity.compose.BackHandler
import androidx.activity.enableEdgeToEdge

@AndroidEntryPoint
class PaymentActivity : ComponentActivity(), PaymentResultListener {

    private val viewModel: PaymentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            TaporiAITheme {
                val uiState by viewModel.uiState.collectAsState()

                Box(modifier = Modifier.fillMaxSize()) {
                    CreditPacksScreen(
                        onBuyClick = { price, chats ->
                            viewModel.setCreditsToAdd(chats)
                            startPayment(this@PaymentActivity, price)
                        }
                    )

                    if (uiState.showLoader) {
                        LoaderDialog()
                    }

                    if (uiState.showSuccessDialog) {
                        SuccessDialog(
                            credits = uiState.newCredits,
                            onDismiss = { viewModel.dismissSuccessDialog() }
                        )
                    }
                }
            }
        }
    }

    override fun onPaymentSuccess(p0: String?) {
        viewModel.handlePaymentSuccess()
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        Toast.makeText(this, "Payment failed: $p1", Toast.LENGTH_SHORT).show()
    }

    fun startPayment(activity: Activity, amount: Int) {
        val checkout = Checkout()
        checkout.setKeyID("rzp_live_REoh16s1CW0W6Q")

        try {
            val options = JSONObject()
            options.put("name", "Spark Studios")
            options.put("description", "Payment for you Tapori AI")
            options.put("currency", "INR")
            options.put("amount", amount * 100)

            val prefill = JSONObject()
            prefill.put("email", "sallyinfo365@gmail.com")
            prefill.put("contact", "+917002601418")
            options.put("prefill", prefill)

            checkout.open(activity, options)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(activity, "Error in payment: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}

@Composable
fun LoaderDialog() {
    Dialog(onDismissRequest = {}) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Ruk zara bhidu credit update ho raha hain...",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
    BackHandler(enabled = true) {}
}

@Composable
fun SuccessDialog(credits: Int, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Theek hai bhidu")
            }
        },
        title = { Text("Payment Successful! 💸") },
        text = {
            Text("Bhai, ab tere account mein $credits credits hain! ✨")
        }
    )
}
