package com.sparkstudios.taporiai.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*

/** Known UPI app packages */
enum class UpiApp(val pkg: String?) {
    ANY(null),
    GOOGLE_PAY("com.google.android.apps.nbu.paisa.user"),
    PHONEPE("com.phonepe.app"),
    PAYTM("net.one97.paytm")
}

/** Request you send to the launcher */
data class UpiRequest(
    val upiId: String,            // e.g. "merchant@upi"
    val payeeName: String,        // e.g. "Acme Store"
    val amount: String,           // "199.00" (2 decimals recommended)
    val note: String = "Payment",
    val txnRef: String = System.currentTimeMillis().toString()
)

/** Result models for callbacks */
data class UpiSuccess(
    val approvalRef: String?,     // UTR/ApprovalRefNo if provided
    val txnRef: String?,          // Your txnRef echoed back (or txnId)
    val raw: String?              // Raw response string from UPI app
)

/** Build UPI deep link */
private fun buildUpiUri(req: UpiRequest): Uri {
    return Uri.Builder()
        .scheme("upi").authority("pay")
        .appendQueryParameter("pa", req.upiId)
        .appendQueryParameter("pn", req.payeeName)
        .appendQueryParameter("tr", req.txnRef)
        .appendQueryParameter("tn", req.note)
        .appendQueryParameter("am", req.amount)
        .appendQueryParameter("cu", "INR")
        .build()
}

/** Parse UPI response string into fields we care about */
private fun parseUpiResponse(raw: String?): Map<String, String> {
    val map = mutableMapOf<String, String>()
    raw?.split("&")?.forEach { part ->
        val idx = part.indexOf("=")
        if (idx > 0) {
            val k = part.substring(0, idx).trim().lowercase()
            val v = part.substring(idx + 1).trim()
            map[k] = v
        }
    }
    return map
}

/** Lightweight classification of result */
private enum class UpiStatus { SUCCESS, SUBMITTED, FAILURE, CANCELLED, UNKNOWN }

private fun classify(map: Map<String, String>, raw: String?): Pair<UpiStatus, UpiSuccess?> {
    val status = map["status"]?.uppercase()
    val code = map["responsecode"]       // often "00" for success (not always present)
    val approval = map["approvalrefno"] ?: map["approvalref"]
    val txnRef = map["txnref"] ?: map["txnid"]

    val ok = (status == "SUCCESS") &&
            ((approval?.isNotBlank() == true) || (txnRef?.isNotBlank() == true)) &&
            (code == null || code == "00")

    val submitted = status == "SUBMITTED"

    return when {
        ok -> UpiStatus.SUCCESS to UpiSuccess(approval, txnRef, raw)
        submitted -> UpiStatus.SUBMITTED to UpiSuccess(approval, txnRef, raw)
        raw == null || raw.isBlank() -> UpiStatus.CANCELLED to null
        status == "FAILURE" -> UpiStatus.FAILURE to null
        else -> UpiStatus.UNKNOWN to UpiSuccess(approval, txnRef, raw)
    }
}

/**
 * Compose utility: returns a function you can call to launch a UPI payment.
 *
 * Usage:
 * val launchUpi = rememberUpiPayLauncher(onSuccess = { ... })
 * launchUpi(UpiRequest(...), preferredApp = UpiApp.GOOGLE_PAY)
 */
@Composable
fun rememberUpiPayLauncher(
    onSuccess: (UpiSuccess) -> Unit,
    onPending: (UpiSuccess) -> Unit = {},
    onFailure: () -> Unit = {},
    onCancelled: () -> Unit = {},
    onUnknown: (UpiSuccess) -> Unit = {}
): (request: UpiRequest, preferredApp: UpiApp) -> Unit {

    // Ensure latest lambdas are used inside launcher
    val onSuccessS by rememberUpdatedState(onSuccess)
    val onPendingS by rememberUpdatedState(onPending)
    val onFailureS by rememberUpdatedState(onFailure)
    val onCancelledS by rememberUpdatedState(onCancelled)
    val onUnknownS by rememberUpdatedState(onUnknown)

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data = result.data
        val raw = data?.getStringExtra("response") ?: data?.dataString
        val parsed = parseUpiResponse(raw)
        val (status, info) = classify(parsed, raw)

        when (status) {
            UpiStatus.SUCCESS   -> info?.let(onSuccessS) ?: onUnknownS(UpiSuccess(null, null, raw))
            UpiStatus.SUBMITTED -> info?.let(onPendingS) ?: onUnknownS(UpiSuccess(null, null, raw))
            UpiStatus.FAILURE   -> onFailureS()
            UpiStatus.CANCELLED -> onCancelledS()
            UpiStatus.UNKNOWN   -> onUnknownS(info ?: UpiSuccess(null, null, raw))
        }
    }

    return remember(launcher) {
        { request: UpiRequest, preferredApp: UpiApp ->
            val uri = buildUpiUri(request)
            var intent = Intent(Intent.ACTION_VIEW, uri)
            preferredApp.pkg?.let { intent.setPackage(it) }
            val chooser = Intent.createChooser(intent, "Pay with spark studios")
            launcher.launch(chooser)
        }
    }
}
