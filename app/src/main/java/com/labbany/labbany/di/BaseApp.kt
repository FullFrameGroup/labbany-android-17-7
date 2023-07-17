package com.labbany.labbany.di

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class BaseApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {

//            androidLogger()
            Level.NONE
            androidContext(this@BaseApp)
            modules(
                listOf(
                    GeneralModules.modules, NetworkModules.modules, ViewModelsModules.modules
                )
            )

        }


    }
}