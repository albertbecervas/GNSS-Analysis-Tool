package com.abecerra.gnssanalysis.core.di

import android.content.Context
import com.abecerra.gnssanalysis.core.navigator.FragmentNavigator
import org.koin.dsl.module

object FragmentModule {

    fun get() = module {
        factory { (context: Context) -> FragmentNavigator(context) }
    }

}