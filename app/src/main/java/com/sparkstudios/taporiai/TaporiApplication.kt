package com.sparkstudios.taporiai

import android.app.Application
import com.sparkstudios.taporiai.di.AppComponent
import com.sparkstudios.taporiai.di.DaggerAppComponent

class TaporiApplication : Application() {
    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.factory().create(this)
    }
}
