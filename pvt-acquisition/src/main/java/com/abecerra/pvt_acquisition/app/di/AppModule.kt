package com.abecerra.pvt_acquisition.app.di

object AppModule {

    fun get() = listOf(
        PresenterModule.get()
    )

}
