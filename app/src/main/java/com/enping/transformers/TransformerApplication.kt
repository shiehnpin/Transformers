package com.enping.transformers

import android.app.Application
import androidx.multidex.MultiDexApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber
import timber.log.Timber.DebugTree

class TransformerApplication : MultiDexApplication(){
    override fun onCreate() {
        super.onCreate()
        startKoin{
            androidContext(this@TransformerApplication)
            modules(appModule, vmModule)
        }

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }
}

