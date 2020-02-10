package com.abecerra.gnssanalysis.app.di

import android.content.Context
import com.abecerra.gnssanalysis.app.navigator.Navigator
import com.abecerra.gnssanalysis.app.navigator.NavigatorImpl
import org.koin.dsl.module

object NavigatorModule {
    fun get() = module {
        single<Navigator> { (context: Context) -> NavigatorImpl(context) }
    }
}
