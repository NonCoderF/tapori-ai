package com.sparkstudios.taporiai.screens

import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PayWithMyUpis(
    payeeName: String = "Spark studios",
    amount: String = "1",
    note: String = "Test payment"
) {
    val context = LocalContext.current
    val selectedUpi = "Q488056855@ybl"

    // Create launcher with callbacks
    val launchUpi = rememberUpiPayLauncher(
        onSuccess = { success ->
            Toast.makeText(context, "Payment Success: ${success.approvalRef}", Toast.LENGTH_LONG)
                .show()
        },
        onPending = {
            Toast.makeText(context, "Payment Pending", LENGTH_SHORT).show()
        },
        onFailure = {
            Toast.makeText(context, "Payment Failed", LENGTH_SHORT).show()
        },
        onCancelled = {
            Toast.makeText(context, "Payment Cancelled", LENGTH_SHORT).show()
        },
        onUnknown = {
            Toast.makeText(context, "Payment Unknown", LENGTH_SHORT).show()
        }
    )

    Column(
        Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Pay ₹$amount to $payeeName", fontSize = 18.sp)
        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                val req = UpiRequest(
                    upiId = selectedUpi,
                    payeeName = payeeName,
                    amount = amount,
                    note = note
                )
                launchUpi(req, UpiApp.ANY)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Pay Now")
        }
    }
}
