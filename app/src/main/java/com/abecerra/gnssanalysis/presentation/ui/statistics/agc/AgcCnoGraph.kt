package com.abecerra.gnssanalysis.presentation.ui.statistics.agc

import android.content.Context
import android.location.GnssMeasurement
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.abecerra.gnssanalysis.R
import com.abecerra.gnssanalysis.presentation.ui.statistics.AgcCNoThreshold
import com.abecerra.gnssanalysis.presentation.ui.statistics.isSelectedBand
import com.abecerra.gnssanalysis.presentation.ui.statistics.setL1E1Axis
import com.abecerra.gnssanalysis.presentation.ui.statistics.setL5E5aAxis
import com.abecerra.pvt.computation.utils.Constants.L1_E1
import com.abecerra.pvt.computation.utils.Constants.L5_E5
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.ScatterData
import com.github.mikephil.charting.data.ScatterDataSet
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet

class AgcCnoGraph : ScatterChart {

    private var agcCNoValues = arrayListOf<Pair<Double, Double>>()

    private var selectedBand: Int = L1_E1

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
        val chartLP = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        layoutParams = chartLP
        description.isEnabled = false
        setDrawBorders(true)
        axisLeft.granularity = 0.1f
        axisRight.isEnabled = false
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 0.1f
        setL1E1Axis()
    }

    fun updateBand(band: Int) {
        selectedBand = band
        agcCNoValues = arrayListOf()
        when (selectedBand) {
            L1_E1 -> setL1E1Axis()
            L5_E5 -> setL5E5aAxis()
        }
    }

    fun plotAgcCNoGraph(measurements: Collection<GnssMeasurement>?) {
        measurements?.let {
            // Obtain measurements for desired frequency band
            val cnos = arrayListOf<Double>()
            val agcs = arrayListOf<Double>()
            it.forEach { meas ->
                if (meas.hasAutomaticGainControlLevelDb() && meas.hasCarrierFrequencyHz()
                    && isSelectedBand(selectedBand, meas.carrierFrequencyHz)
                ) {
                    if (agcCNoValues.size == MAX_AGC_CNO_POINTS) {
                        agcCNoValues.removeAt(0)
                    }
                    cnos.add(meas.cn0DbHz)
                    agcs.add(meas.automaticGainControlLevelDb)
                }
            }

            val avgCno = cnos.average()
            val avgAgc = agcs.average()
            agcCNoValues.add(Pair(avgCno, avgAgc))

            // Generate points with obtained and previous measurements
            val points = arrayListOf<Entry>()
            agcCNoValues.forEach { point ->
                points.add(Entry(point.first.toFloat(), point.second.toFloat())) // x: CNo, y: AGC
            }

            // Obtain threshold and points divided by threshold
            var agcCNoThreshold = AgcCNoThreshold()
            when (selectedBand) {
                L1_E1 -> {
                    agcCNoThreshold = setAgcCNoThreshold(AGC_CNO_N_L1, points)
                }
                L5_E5 -> {
                    agcCNoThreshold = setAgcCNoThreshold(AGC_CNO_N_L5, points)
                }
            }

            // Define style of set
            val thresholdSet = ScatterDataSet(agcCNoThreshold.threshold, "RFI threshold")
            thresholdSet.color = ContextCompat.getColor(context, R.color.agcCnoThreshold)
            thresholdSet.setScatterShape(ScatterShape.CIRCLE)
            thresholdSet.scatterShapeSize = 5.0f
            val nominalPointsSet =
                ScatterDataSet(agcCNoThreshold.nominalPoints, "Nominal conditions")
            nominalPointsSet.color = ContextCompat.getColor(context, R.color.agcCnoNominal)
            nominalPointsSet.setScatterShape(ScatterShape.CIRCLE)
            val interfPointsSet =
                ScatterDataSet(agcCNoThreshold.interferencePoints, "Possible interference")
            interfPointsSet.color = ContextCompat.getColor(context, R.color.agcCnoInterf)
            interfPointsSet.setScatterShape(ScatterShape.CIRCLE)

            // Join sets and plot them on the graph
            val dataSets =
                arrayListOf<IScatterDataSet>(thresholdSet, nominalPointsSet, interfPointsSet)
            val scatterData = ScatterData(dataSets)
            // Do not show labels on each point
            scatterData.setDrawValues(false)
            scatterData.isHighlightEnabled = false
            // Plot points on graph
            data = scatterData
            // Update chart view
            invalidate()

        } // If has stopped, do nothing
    }


    private fun setAgcCNoThreshold(
        n: Float,
        points: ArrayList<Entry>
    ): AgcCNoThreshold {
        val agcCNoThreshold = AgcCNoThreshold()
        // Build points of threshold equation (y=mx+n)
        repeat(1000) { x ->
            agcCNoThreshold.threshold.add(Entry(1.0f * (x - 100), AGC_CNO_M * (x - 100) + n))
        }

        // Take nominal points as points above the threshold
        agcCNoThreshold.nominalPoints =
            points.filter { p -> p.y >= AGC_CNO_M * p.x + n } as? ArrayList<Entry> ?: arrayListOf()
        // Take interference points as points under the threshold
        agcCNoThreshold.interferencePoints =
            points.filter { p -> p.y < AGC_CNO_M * p.x + n } as? ArrayList<Entry> ?: arrayListOf()

        // Sort points over X to plot them
        agcCNoThreshold.nominalPoints.sortBy { p -> p.x }
        agcCNoThreshold.interferencePoints.sortBy { p -> p.x }

        return agcCNoThreshold
    }

    companion object {
        // AGC-CNO threshold values: y=mx+n
        const val AGC_CNO_M = -0.1f
        const val AGC_CNO_N_L1 = 45f
        const val AGC_CNO_N_L5 = 6f

        // Maximum number of points
        const val MAX_AGC_CNO_POINTS = 500
    }
}
