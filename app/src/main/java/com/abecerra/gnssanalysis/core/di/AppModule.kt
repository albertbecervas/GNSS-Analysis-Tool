package com.abecerra.gnssanalysis.core.di

import com.abecerra.gnssanalysis.core.utils.AppSharedPreferences
import org.koin.dsl.module

object AppModule {

    fun get() =
        listOf(
            ViewModelModule.get(),
            UseCaseModule.get(),
            RepositoryModule.get(),
            ApiModule.get(),
            module { single { AppSharedPreferences.getInstance() } }
        )
}