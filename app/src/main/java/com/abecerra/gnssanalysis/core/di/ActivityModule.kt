package com.abecerra.gnssanalysis.core.di

import android.app.Activity
import com.abecerra.gnssanalysis.core.navigator.ActivityNavigator
import org.koin.dsl.module

object ActivityModule {

    fun get() = module {
        factory { (activity: Activity) -> ActivityNavigator(activity) }
    }

}