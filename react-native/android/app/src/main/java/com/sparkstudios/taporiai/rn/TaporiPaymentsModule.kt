package com.sparkstudios.taporiai.rn

import android.content.Intent
import com.facebook.react.bridge.*

class TaporiPaymentsModule(private val context: ReactApplicationContext) : ReactContextBaseJavaModule(context), ActivityEventListener {
    private var pending: Promise? = null
    init { context.addActivityEventListener(this) }
    override fun getName() = "TaporiPayments"
    @ReactMethod fun startCheckout(keyId: String, amountInPaise: Int, description: String, promise: Promise) { val activity = context.currentActivity ?: return promise.reject("NO_ACTIVITY", "Payment requires a foreground activity"); if (pending != null) return promise.reject("PAYMENT_IN_PROGRESS", "A payment is already in progress"); if (keyId.isBlank() || amountInPaise <= 0) return promise.reject("INVALID_PAYMENT", "Payment configuration is invalid"); pending = promise; activity.startActivityForResult(Intent(activity, TaporiPaymentActivity::class.java).apply { putExtra(TaporiPaymentActivity.KEY_ID, keyId); putExtra(TaporiPaymentActivity.AMOUNT, amountInPaise); putExtra(TaporiPaymentActivity.DESCRIPTION, description) }, REQUEST_CODE) }
    override fun onActivityResult(activity: android.app.Activity, requestCode: Int, resultCode: Int, data: Intent?) { if (requestCode != REQUEST_CODE) return; val result = pending ?: return; pending = null; if (resultCode == android.app.Activity.RESULT_OK) result.resolve(data?.getStringExtra(TaporiPaymentActivity.PAYMENT_ID) ?: "") else result.reject("PAYMENT_FAILED", data?.getStringExtra(TaporiPaymentActivity.ERROR) ?: "Payment failed") }
    override fun onNewIntent(intent: Intent) = Unit
    override fun invalidate() { context.removeActivityEventListener(this); pending?.reject("MODULE_DESTROYED", "Payment was cancelled"); pending = null; super.invalidate() }
    companion object { const val REQUEST_CODE = 7402 }
}
