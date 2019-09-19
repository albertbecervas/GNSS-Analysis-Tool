package com.abecerra.gnssanalysis.presentation.ui.statistics

import com.github.mikephil.charting.charts.ScatterChart


const val MAX_CNO_L1 = 50f // dB
const val MIN_CNO_L1 = 0f // dB
const val MAX_CNO_L5 = 40f // dB
const val MIN_CNO_L5 = -10f // dB
const val MAX_AGC_L1 = 60f // dB-Hz
const val MIN_AGC_L1 = 30f // dB-Hz
const val MAX_AGC_L5 = 20f // dB-Hz
const val MIN_AGC_L5 = -10f // dB-Hz

const val MIN_ELEV = 0f // º
const val MAX_ELEV = 90f // º

fun ScatterChart.setL1E1Axis() {
    this.xAxis.axisMinimum = MIN_CNO_L1
    this.xAxis.axisMaximum = MAX_CNO_L1
    axisLeft.axisMinimum = MIN_AGC_L1
    axisLeft.axisMaximum = MAX_AGC_L1
}

fun ScatterChart.setL5E5aAxis() {
    this.xAxis.axisMinimum = MIN_CNO_L5
    this.xAxis.axisMaximum = MAX_CNO_L5
    axisLeft.axisMinimum = MIN_AGC_L5
    axisLeft.axisMaximum = MAX_AGC_L5
}

fun ScatterChart.setElevL1Axis() {
    this.xAxis.axisMinimum = MIN_ELEV
    this.xAxis.axisMaximum = MAX_ELEV
    axisLeft.axisMinimum = MIN_AGC_L1
    axisLeft.axisMaximum = MAX_AGC_L1
}

fun ScatterChart.setElevL5Axis() {
    this.xAxis.axisMinimum = MIN_ELEV
    this.xAxis.axisMaximum = MAX_ELEV
    axisLeft.axisMinimum = MIN_AGC_L5
    axisLeft.axisMaximum = MAX_AGC_L5
}

