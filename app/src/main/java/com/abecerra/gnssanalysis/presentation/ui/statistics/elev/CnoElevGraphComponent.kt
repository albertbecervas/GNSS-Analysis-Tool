package com.abecerra.gnssanalysis.presentation.ui.statistics.elev

import android.content.Context
import android.location.GnssStatus
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.abecerra.gnssanalysis.R
import kotlinx.android.synthetic.main.view_chart.view.rlGraph

class CnoElevGraphComponent : RelativeLayout {

    private val cnoElevGraph: CnoElevGraph

    constructor(context: Context) : super(context) {
        cnoElevGraph = CnoElevGraph(context)
        this.initViews(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        cnoElevGraph = CnoElevGraph(context, attrs)
        this.initViews(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) :
            super(context, attrs, defStyle) {
        cnoElevGraph = CnoElevGraph(context, attrs, defStyle)
        this.initViews(context)
    }

    private fun initViews(context: Context) {
        val view = View.inflate(context, R.layout.view_chart, this)
        addView(view)
        view.rlGraph.addView(cnoElevGraph)
    }

    fun updateBand(band: Int) {
        cnoElevGraph.updateBand(band)
    }

    fun plotElevCNoGraph(status: GnssStatus) {
        cnoElevGraph.plotElevCNoGraph(status)
    }
}
