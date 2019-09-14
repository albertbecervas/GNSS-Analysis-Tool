package com.abecerra.gnssanalysis.core.di

import com.abecerra.gnssanalysis.core.computation.presenter.PvtPresenterImpl
import com.abecerra.gnssanalysis.core.computation.presenter.PvtServiceContract
import org.koin.dsl.module

object PresenterModule {

    fun get() = module {
        single<PvtServiceContract.PvtPresenter> { PvtPresenterImpl(get(), get()) }
    }
}
