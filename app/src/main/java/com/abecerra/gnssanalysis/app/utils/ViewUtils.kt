package com.abecerra.gnssanalysis.app.utils

import android.widget.Toast
import com.abecerra.gnssanalysis.R
import com.abecerra.gnssanalysis.app.App
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.io.InputStreamReader

val context = App.getAppContext()

fun toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(context, message, duration).show()
}

@Throws(Exception::class)
fun getStringFromFile(filePath: String): String {
    val fl = File(filePath)
    val fin = FileInputStream(fl)
    val ret = convertStreamToString(fin)
    //Make sure you close all streams.
    fin.close()
    return ret
}

@Throws(Exception::class)
fun convertStreamToString(`is`: InputStream): String {
    val reader = BufferedReader(InputStreamReader(`is`))
    val sb = StringBuilder()
    var line: String? = reader.readLine()
    while (line != null) {
        sb.append(line).append("\n")
        line = reader.readLine()
    }
    reader.close()
    return sb.toString()
}

fun getLegendColor(position: Int): Int {
    return when (position) {
        0 -> R.color.colorLegend1
        1 -> R.color.colorLegend2
        2 -> R.color.colorLegend3
        3 -> R.color.colorLegend4
        4 -> R.color.colorLegend5
        else -> R.color.colorLegend1
    }
}

fun getModeIcon(color: Int): Float {
    return when (color) {
        0 -> BitmapDescriptorFactory.HUE_RED
        1 -> BitmapDescriptorFactory.HUE_ORANGE
        2 -> BitmapDescriptorFactory.HUE_BLUE
        3 -> BitmapDescriptorFactory.HUE_CYAN
        4 -> BitmapDescriptorFactory.HUE_YELLOW
        else -> BitmapDescriptorFactory.HUE_RED
    }
}
