package com.sparkstudios.taporiai

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sparkstudios.taporiai.presentation.chat.ChatViewModel
import com.sparkstudios.taporiai.screens.ChatScreen
import com.sparkstudios.taporiai.screens.SignInScreen
import com.sparkstudios.taporiai.ui.theme.TaporiAITheme
import com.sparkstudios.taporiai.utils.Prefs

import androidx.activity.enableEdgeToEdge

sealed class Screen(val route: String) {
    object SignIn : Screen("sign_in")
    object Home : Screen("home")

}

class MainActivity : ComponentActivity() {

    private val viewModelFactory: ViewModelProvider.Factory by lazy {
        (application as TaporiApplication).appComponent.viewModelFactory()
    }

    private val chatViewModel: ChatViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            TaporiAITheme {

                AppNavigation()
            }
        }
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
                    },
                    viewModel = chatViewModel
                )
            }
        }
    }
}
