package com.abecerra.gnssanalysis.app.di

import com.abecerra.pvt_acquisition.app.logger.GnssMeasLogger
import com.abecerra.pvt_acquisition.app.logger.GnssMeasLoggerImpl
import org.koin.dsl.module

object LoggerModule {

    fun get() = module {
        single<GnssMeasLogger> { GnssMeasLoggerImpl() }
    }
}
