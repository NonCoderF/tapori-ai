package com.sparkstudios.taporiai

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@Composable
fun SignInScreen(navController: NavController) {
    val context = LocalContext.current
    var googleSignInClient: GoogleSignInClient? = null

    googleSignInClient = GoogleSignIn.getClient(
        context,
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
    )

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account: GoogleSignInAccount = task.getResult(ApiException::class.java)!!
            Prefs.saveUser(context, account.id ?: "", account.displayName)
            Log.e("TAG", "Google Sign In Result : ${account.id}")
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.SignIn.route) { inclusive = true }
            }
        } catch (e: ApiException) {
            e.printStackTrace()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { launcher.launch(googleSignInClient.signInIntent) }) {
            Text("Sign in with Google", fontSize = 18.sp)
        }
    }
}
