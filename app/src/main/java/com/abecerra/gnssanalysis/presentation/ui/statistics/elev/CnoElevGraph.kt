package com.abecerra.gnssanalysis.presentation.ui.statistics.elev

import android.content.Context
import android.location.GnssStatus
import android.util.AttributeSet
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import com.abecerra.gnssanalysis.R
import com.abecerra.gnssanalysis.core.utils.filterGnssStatus
import com.abecerra.gnssanalysis.presentation.ui.skyplot.SkyPlotFragment
import com.abecerra.gnssanalysis.presentation.ui.statistics.obtainCnoElevValues
import com.abecerra.gnssanalysis.presentation.ui.statistics.setElevL1Axis
import com.abecerra.gnssanalysis.presentation.ui.statistics.setElevL5Axis
import com.abecerra.pvt.computation.utils.Constants
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.ScatterData
import com.github.mikephil.charting.data.ScatterDataSet
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet

class CnoElevGraph : ScatterChart {

    private var selectedBand: Int = Constants.L1_E1

    constructor(context: Context) : super(context) {
        this.initViews()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.initViews()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) :
            super(context, attrs, defStyle) {
        this.initViews()
    }

    private fun initViews() {
        val chartLP = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT
        )
        layoutParams = chartLP
        description.isEnabled = false
        setDrawBorders(true)
        axisLeft.granularity = 0.1f
        axisRight.isEnabled = false
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 0.1f
        legend.isEnabled = true
        xAxis.labelCount = 10
        setElevL1Axis()
    }


    fun updateBand(band: Int) {
        selectedBand = band
        when (selectedBand) {
            Constants.L1_E1 -> setElevL1Axis()
            Constants.L5_E5 -> setElevL5Axis()
        }
    }

    fun plotElevCNoGraph(status: GnssStatus) {
        // Obtain status separately for GPS and GAL
        val gpsGnssStatus =
            filterGnssStatus(status, SkyPlotFragment.Companion.CONSTELLATION.GPS)
        val galGnssStatus =
            filterGnssStatus(status, SkyPlotFragment.Companion.CONSTELLATION.GALILEO)

        // Obtain desired values of Status: svid, elevation and CNo for given frequency band
        val gpsValues = obtainCnoElevValues(selectedBand, gpsGnssStatus)
        val galValues = obtainCnoElevValues(selectedBand, galGnssStatus)

        // Generate points
        val gpsPoints = arrayListOf<Entry>()
        val galPoints = arrayListOf<Entry>()
        gpsValues.forEach {
            gpsPoints.add(Entry(it.elevation, it.cNo))
        }
        galValues.forEach {
            galPoints.add(Entry(it.elevation, it.cNo))
        }

        // Sort points by elevation to plot them
        gpsPoints.sortBy { point -> point.x }
        galPoints.sortBy { point -> point.x }

        // Define style of sets
        val gpsPointsSet = ScatterDataSet(gpsPoints, "GPS")
        val galPointsSet = ScatterDataSet(galPoints, "Galileo")

        gpsPointsSet.color = ContextCompat.getColor(context, R.color.gpsColor)
        gpsPointsSet.setScatterShape(ScatterShape.CIRCLE)
        galPointsSet.color = ContextCompat.getColor(context, R.color.galColor)
        galPointsSet.setScatterShape(ScatterShape.CIRCLE)

        // Join sets and plot them on the graph
        val dataSets = arrayListOf<IScatterDataSet>(gpsPointsSet, galPointsSet)
        val scatterData = ScatterData(dataSets)
        // Do not show labels on each point
        scatterData.setDrawValues(false)
        scatterData.isHighlightEnabled = false
        // Plot points on graph
        data = scatterData
        // Update chart view
        invalidate()
    }

}
