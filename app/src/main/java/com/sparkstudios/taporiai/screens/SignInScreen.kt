package com.sparkstudios.taporiai.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.sparkstudios.tapori.ai.chatbot.R
import com.sparkstudios.taporiai.Screen
import com.sparkstudios.taporiai.utils.CLIENT_ID
import com.sparkstudios.taporiai.utils.Prefs

@Composable
fun SignInScreen(navController: NavController) {
    val context = LocalContext.current
    var googleSignInClient: GoogleSignInClient? = null

    googleSignInClient = GoogleSignIn.getClient(
        context,
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(CLIENT_ID)
            .build()
    )

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account: GoogleSignInAccount = task.getResult(ApiException::class.java)!!
            Prefs.saveUser(context, account.idToken ?: "", account.displayName)
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.SignIn.route) { inclusive = true }
            }
        } catch (e: ApiException) {
            e.printStackTrace()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF101820)), // Tapori dark background
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.signin_background_image),
            contentDescription = "Tapori Logo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.85f)))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Arre Bhidu! 👊",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            }

            Button(
                onClick = { launcher.launch(googleSignInClient.signInIntent) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Yellow,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .height(56.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.google_icon),
                    contentDescription = "Google",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Google se sign in kar re",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

//            Button(
//                onClick = {
//                    navController.navigate(Screen.Home.route) {
//                        popUpTo(Screen.SignIn.route) { inclusive = true }
//                    }
//                },
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = Color.Yellow,
//                    contentColor = Color.Black
//                ),
//                shape = RoundedCornerShape(50),
//                modifier = Modifier
//                    .padding(horizontal = 32.dp)
//                    .height(56.dp)
//            ) {
//                Image(
//                    painter = painterResource(id = R.drawable.outline_synagogue_24),
//                    contentDescription = "Google",
//                    modifier = Modifier.size(24.dp)
//                )
//                Spacer(modifier = Modifier.width(8.dp))
//                Text(
//                    text = "Guest hai tu, chal proceed kar re!",
//                    fontSize = 18.sp,
//                    fontWeight = FontWeight.SemiBold
//                )
//            }
        }
    }
}

