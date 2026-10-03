package com.sparkstudios.taporiai.rn

import android.os.Build
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod

class TaporiDeviceModule(context: ReactApplicationContext) : ReactContextBaseJavaModule(context) {
    override fun getName() = "TaporiDevice"
    @ReactMethod fun getDeviceInfo(promise: Promise) { try { promise.resolve(Arguments.createMap().apply { putString("manufacturer", Build.MANUFACTURER); putString("model", Build.MODEL); putInt("sdk", Build.VERSION.SDK_INT) }) } catch (error: Exception) { promise.reject("DEVICE_INFO_FAILED", "Unable to read device information", error) } }
}
