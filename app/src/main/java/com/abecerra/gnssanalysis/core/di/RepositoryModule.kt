package com.abecerra.gnssanalysis.core.di

import com.abecerra.gnssanalysis.core.computation.repository.PvtRepository
import com.abecerra.gnssanalysis.core.computation.repository.PvtRepositoryImpl
import org.koin.dsl.module

object RepositoryModule {

    fun get() = module {
        single<PvtRepository> { PvtRepositoryImpl() }
    }
}
