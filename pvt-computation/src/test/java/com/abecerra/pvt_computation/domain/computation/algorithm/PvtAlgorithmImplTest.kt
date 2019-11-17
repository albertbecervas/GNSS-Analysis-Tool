package com.abecerra.pvt_computation.domain.computation.algorithm

import com.abecerra.pvt_computation.data.input.PvtInputData
import com.abecerra.pvt_computation.data.input.mapper.PvtAlgorithmInputDataMapper
import com.abecerra.pvt_computation.data.output.PvtLatLng
import com.abecerra.pvt_computation.domain.computation.algorithm.leastsquares.LeastSquaresAlgorithm
import com.abecerra.pvt_computation.domain.computation.algorithm.leastsquares.LeastSquaresAlgorithmImpl
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.junit.Before
import org.junit.Test

class PvtAlgorithmImplTest {

    private lateinit var pvtAlgorithm: PvtAlgorithm

    private lateinit var pvtInputDataList: ArrayList<PvtInputData>

    @Before
    fun setUp() {
        val leastSquaresAlgorithm: LeastSquaresAlgorithm = LeastSquaresAlgorithmImpl()
        pvtAlgorithm = PvtAlgorithmImpl(leastSquaresAlgorithm)
        pvtInputDataList = arrayListOf()
        repeat(10) {
            pvtInputDataList.add(getPvtInputDataFromFile("input/sunday/input_${it + 1}.txt"))
        }
    }

    @Test
    fun executePvtComputationAlgorithm() {
        pvtInputDataList.forEach { pvtInputData ->
            pvtInputData.computationSettings.forEach {
                val pvtAlgorithmInputData = PvtAlgorithmInputDataMapper.mapFromPvtInputData(pvtInputData, it)
                val outputData = pvtAlgorithm.executePvtAlgorithm(pvtAlgorithmInputData)
                val location = outputData?.pvtFix?.pvtLatLng
                print(
                    "\n====================================================\n" +
                            "Reference Position: ${pvtInputData.refLocation.llaLocation.latitude}," +
                            " ${pvtInputData.refLocation.llaLocation.longitude}\n"
                )
                printOutput(location)
                assert(outputData != null)
            }
        }
    }

    fun Double.round(decimals: Int): Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return kotlin.math.round(this * multiplier) / multiplier
    }

    private fun printOutput(location: PvtLatLng?) {
        print("Computed Position:${location?.lat},${location?.lng}")
    }

    private fun getPvtInputDataFromFile(fileName: String): PvtInputData {

        //Getting the file
        val fileInput = this.javaClass.classLoader?.getResourceAsStream(fileName)
        val pvtInputDataString = fileInput?.bufferedReader().use { it?.readText() }
        val type = object : TypeToken<PvtInputData>() {}.type

        return if (pvtInputDataString != null) {
            //parse data into the specified object
            Gson().fromJson<PvtInputData>(pvtInputDataString, type)
        } else {
            PvtInputData()
        }
    }
}
