package com.abecerra.gnssanalysis.presentation.ui.statistics

import com.abecerra.gnssanalysis.app.base.BaseFragment

abstract class BaseGraphFragment : BaseFragment() {

    abstract fun getGraphInformation(): String

}
