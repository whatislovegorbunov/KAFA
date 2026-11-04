package com.example.kafa

import android.app.Application
import com.example.kafa.di.AppComponent
import com.example.kafa.di.DaggerAppComponent

class KafaApp : Application() {
    val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(this)
    }
}
