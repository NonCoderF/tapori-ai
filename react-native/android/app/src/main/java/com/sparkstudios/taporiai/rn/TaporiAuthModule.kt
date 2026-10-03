package com.sparkstudios.taporiai.rn

import android.content.Intent
import com.facebook.react.bridge.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

class TaporiAuthModule(private val context: ReactApplicationContext) : ReactContextBaseJavaModule(context), ActivityEventListener {
    private var signInPromise: Promise? = null
    init { context.addActivityEventListener(this) }
    override fun getName() = "TaporiAuth"
    @ReactMethod fun signIn(clientId: String, promise: Promise) { val activity = context.currentActivity ?: return promise.reject("NO_ACTIVITY", "Sign-in requires a foreground activity"); if (signInPromise != null) return promise.reject("SIGN_IN_IN_PROGRESS", "Sign-in is already in progress"); signInPromise = promise; val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(clientId).requestEmail().build(); activity.startActivityForResult(GoogleSignIn.getClient(activity, options).signInIntent, REQUEST_CODE) }
    @ReactMethod fun refresh(clientId: String, promise: Promise) { val activity = context.currentActivity ?: return promise.reject("NO_ACTIVITY", "Refresh requires a foreground activity"); val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(clientId).requestEmail().build(); GoogleSignIn.getClient(activity, options).silentSignIn().addOnCompleteListener { task -> if (task.isSuccessful) { val account = task.result; promise.resolve(Arguments.createMap().apply { putString("idToken", account.idToken); putString("displayName", account.displayName) }) } else promise.reject("GOOGLE_REFRESH_FAILED", "Google session refresh failed", task.exception) } }
    @ReactMethod fun signOut(promise: Promise) { val activity = context.currentActivity ?: return promise.resolve(null); GoogleSignIn.getClient(activity, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut().addOnCompleteListener { promise.resolve(null) } }
    override fun onActivityResult(activity: android.app.Activity, requestCode: Int, resultCode: Int, data: Intent?) { if (requestCode != REQUEST_CODE) return; val promise = signInPromise ?: return; signInPromise = null; try { val account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException::class.java); promise.resolve(Arguments.createMap().apply { putString("idToken", account.idToken); putString("displayName", account.displayName) }) } catch (error: ApiException) { promise.reject("GOOGLE_SIGN_IN_FAILED", "Google sign-in failed", error) } }
    override fun onNewIntent(intent: Intent) = Unit
    override fun invalidate() { context.removeActivityEventListener(this); signInPromise?.reject("MODULE_DESTROYED", "Sign-in was cancelled"); signInPromise = null; super.invalidate() }
    companion object { const val REQUEST_CODE = 7401 }
}
