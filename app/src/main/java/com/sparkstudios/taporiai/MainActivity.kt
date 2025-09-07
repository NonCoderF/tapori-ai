package com.sparkstudios.taporiai

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import com.sparkstudios.taporiai.screens.ChatScreen
import com.sparkstudios.taporiai.screens.CreditPacksScreen
import com.sparkstudios.taporiai.screens.SignInScreen
import com.sparkstudios.taporiai.ui.theme.TaporiAITheme
import com.sparkstudios.taporiai.utils.Prefs
import kotlin.toString

sealed class Screen(val route: String) {
    object SignIn : Screen("sign_in")
    object Home : Screen("home")
}

class MainActivity : ComponentActivity(), PaymentResultListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TaporiAITheme {
                AppNavigation()
            }
        }
    }

    override fun onPaymentSuccess(p0: String?) {
        Log.e("TAG", "Paymnet success : " + p0.toString())
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        Log.e("TAG", "Paymnet fail : " + p1.toString())
    }

    @Composable
    fun AppNavigation() {
        val context = LocalContext.current
        val navController = rememberNavController()
        val startDestination = if (Prefs.getUserIdToken(context) == null) Screen.SignIn.route else Screen.Home.route

        NavHost(navController = navController, startDestination = startDestination) {
            composable(Screen.SignIn.route) { SignInScreen(navController) }
            composable(Screen.Home.route) {
                ChatScreen(navController,
                    onClose = { finish() },
                    onPaymentInvoked = {
                        startActivity(Intent(this@MainActivity, PaymentActivity::class.java))
                    }
                )
            }
        }
    }
}
