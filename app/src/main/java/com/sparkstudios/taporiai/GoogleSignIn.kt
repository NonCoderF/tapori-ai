package com.sparkstudios.taporiai

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

const val CLIENT_ID = "982063479058-h3d3h9s9nsreb241e74okdpgsrmdk36l.apps.googleusercontent.com"

fun refreshToken(context: Context, onRefreshed: () -> Unit = {}, onFailure: () -> Unit = {}) = {
    val account = GoogleSignIn.getLastSignedInAccount(context)
    if (account != null) {
        val googleSignInClient = GoogleSignIn.getClient(
            context,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(CLIENT_ID)
                .requestEmail()
                .build()
        )
        val task = googleSignInClient.silentSignIn()

        task.addOnCompleteListener { t ->
            if (t.isSuccessful) {
                val newIdToken = t.result?.idToken
                Prefs.saveUser(context, newIdToken ?: "", account.displayName)
                onRefreshed.invoke()
            } else {
                onFailure.invoke()
            }
        }
    }
}

fun logout(context: Context) = {
    GoogleSignIn.getClient(context, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut()
    Prefs.clearUser(context)
}