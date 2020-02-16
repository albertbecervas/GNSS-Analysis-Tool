package com.abecerra.gnssanalysis.app.di

import com.abecerra.gnssanalysis.presentation.ui.position.PvtComputationViewModel
import com.abecerra.gnssanalysis.presentation.ui.skyplot.SkyPlotViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

object ViewModelModule {

    fun get() = module {
        viewModel { PvtComputationViewModel(get()) }
        viewModel { SkyPlotViewModel() }
    }
}
