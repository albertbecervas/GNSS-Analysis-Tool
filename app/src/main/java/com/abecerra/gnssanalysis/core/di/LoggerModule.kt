package com.abecerra.gnssanalysis.core.di

import com.abecerra.gnssanalysis.core.logger.GnssMeasLogger
import org.koin.dsl.module

object LoggerModule {

    fun get() = module {
        single { GnssMeasLogger() }
    }
}
