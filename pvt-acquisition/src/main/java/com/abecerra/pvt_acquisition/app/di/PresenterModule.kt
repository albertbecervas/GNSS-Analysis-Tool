package com.abecerra.pvt_acquisition.app.di

import com.abecerra.pvt_acquisition.acquisition.presenter.PvtPresenterImpl
import com.abecerra.pvt_acquisition.acquisition.presenter.PvtServiceContract
import org.koin.dsl.module

object PresenterModule {

    fun get() = module {
        single<PvtServiceContract.PvtPresenter> { PvtPresenterImpl() }
    }

}
