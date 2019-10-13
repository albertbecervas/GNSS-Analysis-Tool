package com.abecerra.gnssanalysis.app

import android.content.Context
import androidx.multidex.MultiDexApplication
import com.abecerra.gnssanalysis.BuildConfig
import com.abecerra.gnssanalysis.app.di.AppModule
import org.koin.core.context.startKoin
import timber.log.Timber
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric

class App : MultiDexApplication() {

    companion object {

        lateinit var INSTANCE: App

        fun getAppContext(): App = INSTANCE

        fun get(context: Context): App {
            return context.applicationContext as App
        }
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        initKoin()
        initTimber()
        Fabric.with(this, Crashlytics())
    }

    private fun initKoin() {
        startKoin {
            modules(AppModule.get())
        }
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
