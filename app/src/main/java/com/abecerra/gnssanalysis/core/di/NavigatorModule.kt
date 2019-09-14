package com.abecerra.gnssanalysis.core.di

import android.content.Context
import com.abecerra.gnssanalysis.core.navigator.Navigator
import com.abecerra.gnssanalysis.core.navigator.NavigatorImpl
import org.koin.dsl.module

object NavigatorModule {
    fun get() = module {
        single<Navigator> { (context: Context) -> NavigatorImpl(context) }
    }
}
