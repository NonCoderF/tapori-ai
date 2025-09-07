package com.sparkstudios.taporiai

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import com.sparkstudios.taporiai.network.CreditRequest
import com.sparkstudios.taporiai.network.RetrofitClient
import com.sparkstudios.taporiai.screens.CreditPacksScreen
import com.sparkstudios.taporiai.ui.theme.TaporiAITheme
import com.sparkstudios.taporiai.utils.Prefs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class PaymentActivity : ComponentActivity(), PaymentResultListener {

    private var creditsToAdd = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TaporiAITheme {
                CreditPacksScreen(
                    onBuyClick = { price, chats ->
                        creditsToAdd = chats
                        startPayment(this, price)
                    }
                )

                BackHandler {  }
            }
        }
    }


    override fun onPaymentSuccess(p0: String?) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.addCredit(
                    CreditRequest(
                        idToken = Prefs.getUserIdToken(this@PaymentActivity) ?: "",
                        creditsToAdd = creditsToAdd
                    )
                )

                if (response.isSuccessful) {
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(
                            this@PaymentActivity,
                            response.body()?.message,
                            LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("Exception", e.toString())
            } finally {

            }
        }
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        Toast.makeText(this, "Payment failed: $p1", Toast.LENGTH_SHORT).show()
    }

    fun startPayment(activity : Activity, amount : Int) {
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

