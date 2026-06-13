package com.sparkstudios.taporiai.utils

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

import com.sparkstudios.taporiai.BuildConfig

val CLIENT_ID = BuildConfig.GOOGLE_CLIENT_ID

fun refreshToken(context: Context, onRefreshed: () -> Unit = {}, onFailure: () -> Unit = {}) {
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
                val newIdToken = t.result?.idToken ?: ""
                Prefs.saveUser(context, newIdToken, account.displayName)
                onRefreshed.invoke()
            } else {
                onFailure.invoke()
            }
        }
    } else {
        onFailure.invoke()
    }
}

fun logout(context: Context) {
    val googleSignInClient = GoogleSignIn.getClient(
        context,
        GoogleSignInOptions.DEFAULT_SIGN_IN
    )
    Prefs.clearUser(context)
    context.cacheDir?.deleteRecursively()
    context.filesDir?.deleteRecursively()

    googleSignInClient.signOut().addOnCompleteListener {
        googleSignInClient.revokeAccess()
    }
}
