package com.abecerra.gnssanalysis.app.di

import com.abecerra.gnssanalysis.app.utils.AppSharedPreferences
import org.koin.dsl.module

object AppModule {

    fun get() =
        listOf(
            ViewModelModule.get(),
            UseCaseModule.get(),
            ApiModule.get(),
            NavigatorModule.get(),
            LoggerModule.get(),
            module { single { AppSharedPreferences.getInstance() } }
        )
}
