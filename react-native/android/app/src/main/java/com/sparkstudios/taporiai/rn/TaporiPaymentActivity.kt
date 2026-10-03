package com.sparkstudios.taporiai.rn

import android.app.Activity
import android.os.Bundle
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject

class TaporiPaymentActivity : Activity(), PaymentResultListener {
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); try { val checkout = Checkout().apply { setKeyID(intent.getStringExtra(KEY_ID)) }; val options = JSONObject().apply { put("name", "Tapori AI"); put("description", intent.getStringExtra(DESCRIPTION)); put("currency", "INR"); put("amount", intent.getIntExtra(AMOUNT, 0)) }; checkout.open(this, options) } catch (error: Exception) { setFailure(error.message ?: "Unable to start payment") } }
    override fun onPaymentSuccess(paymentId: String?) { setResult(RESULT_OK, intent.putExtra(PAYMENT_ID, paymentId)); finish() }
    override fun onPaymentError(code: Int, description: String?) { setFailure(description ?: "Payment failed ($code)") }
    private fun setFailure(message: String) { setResult(RESULT_CANCELED, intent.putExtra(ERROR, message)); finish() }
    companion object { const val KEY_ID = "key_id"; const val AMOUNT = "amount"; const val DESCRIPTION = "description"; const val PAYMENT_ID = "payment_id"; const val ERROR = "error" }
}
