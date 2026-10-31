package com.enekocm.securevault

import android.app.Application
import di.AppContainer

class SecureVaultApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
