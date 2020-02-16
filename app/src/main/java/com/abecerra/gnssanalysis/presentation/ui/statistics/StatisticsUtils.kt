package com.abecerra.gnssanalysis.presentation.ui.statistics

import android.content.Context
import android.widget.RelativeLayout
import com.abecerra.gnssanalysis.presentation.ui.statistics.StatisticsFragment.Companion.L1_E1
import com.abecerra.gnssanalysis.presentation.ui.statistics.StatisticsFragment.Companion.L5_E5
import com.abecerra.pvt_acquisition.data.GnssStatus
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.google.android.gms.maps.model.LatLng

const val BAND1_DOWN_THRES = 1575000000
const val BAND1_UP_THRES = 1576000000
const val BAND5_DOWN_THRES = 1176000000
const val BAND5_UP_THRES = 1177000000

class SatElevCNo(var svid: Int, var elevation: Float, var cNo: Float)
class AgcCNoThreshold(
    var threshold: ArrayList<Entry> = arrayListOf(),
    var nominalPoints: ArrayList<Entry> = arrayListOf(),
    var interferencePoints: ArrayList<Entry> = arrayListOf()
)

fun createScatterChart(
    context: Context?,
    xMin: Float,
    xMax: Float,
    yMin: Float,
    yMax: Float
): ScatterChart? {
    val scatterChart = ScatterChart(context)

    scatterChart.let { chart ->
        chart.xAxis.axisMinimum = xMin
        chart.xAxis.axisMaximum = xMax
        chart.axisLeft.axisMinimum = yMin
        chart.axisLeft.axisMaximum = yMax
        chart.axisRight.isEnabled = false

        val chartLP = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT
        )
        chart.layoutParams = chartLP
        chart.description.isEnabled = false
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.xAxis.granularity = 0.1f
        chart.axisLeft.granularity = 0.1f
        chart.setDrawBorders(true)
    }

    return scatterChart
}

fun obtainCnoElevValues(selectedBand: Int, status: GnssStatus): ArrayList<SatElevCNo> {
    val satElevCNoList = arrayListOf<SatElevCNo>()
    with(status) {
        for (sat in 0 until satelliteCount) {
            if (isSelectedBand(selectedBand, getCarrierFrequencyHz(sat))) { // Add it if it is in selected band
                // Obtain point information
                satElevCNoList.add(
                    SatElevCNo(
                        getSvid(sat),
                        getElevationDegrees(sat),
                        getCn0DbHz(sat)
                    )
                )
            } // If not in selected frequency, do nothing
        } // End for
    }
    return satElevCNoList
}

fun setAgcCNoThreshold(
    m: Float,
    n: Float,
    points: ArrayList<Entry>
): AgcCNoThreshold {
    val agcCNoThreshold = AgcCNoThreshold()
    // Build points of threshold equation (y=mx+n)
    repeat(1000) { x ->
        agcCNoThreshold.threshold.add(Entry(1.0f * (x - 100), m * (x - 100) + n))
    }

    // Take nominal points as points above the threshold
    agcCNoThreshold.nominalPoints = points.filter { p -> p.y >= m * p.x + n } as? ArrayList<Entry> ?: arrayListOf()
    // Take interference points as points under the threshold
    agcCNoThreshold.interferencePoints = points.filter { p -> p.y < m * p.x + n } as? ArrayList<Entry> ?: arrayListOf()

    // Sort points over X to plot them
    agcCNoThreshold.nominalPoints.sortBy { p -> p.x }
    agcCNoThreshold.interferencePoints.sortBy { p -> p.x }

    return agcCNoThreshold
}

fun isSelectedBand(selectedBand: Int, carrierFrequencyHz: Float): Boolean {
    var isSelected = false
    when (selectedBand) {
        L1_E1 -> {
            if ((carrierFrequencyHz > BAND1_DOWN_THRES && // If carrierFrequencyHz inside L1_E1 band
                        carrierFrequencyHz < BAND1_UP_THRES) ||
                (carrierFrequencyHz == 0.0f)
            ) {
                isSelected = true
            }
        }
        L5_E5 -> {
            if (carrierFrequencyHz > BAND5_DOWN_THRES && // If carrierFrequencyHz inside L5_E5 band
                carrierFrequencyHz < BAND5_UP_THRES
            ) {
                isSelected = true
            }
        }
    }
    return isSelected
}

/**
 * Function used to compute the error between two points in Latitude, Longitude
 * as the distance in meters on East and North axis.
 */
fun computeErrorNE(refPos: LatLng, refAlt: Float, compPos: LatLng): DoubleArray {

    val deltaLat = (compPos.latitude - refPos.latitude) * Math.PI / 180.0
    val deltaLng = (compPos.longitude - refPos.longitude) * Math.PI / 180.0

    // Declare the required WGS-84 ellipsoid parameters
    val a = 6378137.0     // Semi-major axis
    val b = 6356752.31425 // Semi-minor axis

    // Compute the second eccentricity
    val e2 = (Math.pow(a, 2.0) - Math.pow(b, 2.0)) / Math.pow(a, 2.0)

    // Compute additional parameter required in the processing
    val W = Math.sqrt(1.0 - e2 * Math.pow(Math.sin(refPos.latitude), 2.0))

    // Compute the meridian radius of curvature at the given latitude
    val M = a * (1.0 - e2) / Math.pow(W, 3.0)

    // Compute the the prime vertical radius of curvature at the given latitude
    val N = a / W

    // Compute the differences on North and East
    val deltaN = (deltaLat * (M + refAlt))
    val deltaE = -(deltaLng * (N + refAlt) * Math.cos(refPos.latitude))

    return doubleArrayOf(deltaN, deltaE)
}
