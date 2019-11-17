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
                val pvtAlgorithmInputData =
                    PvtAlgorithmInputDataMapper.mapFromPvtInputData(pvtInputData, it)
                val outputData = pvtAlgorithm.executePvtAlgorithm(pvtAlgorithmInputData)
                val location = outputData?.pvtFix?.pvtLatLng
                printOutput(pvtInputData, location)
                assert(outputData != null)
            }
        }
    }

    private fun printOutput(pvtInputData: PvtInputData, location: PvtLatLng?) {
        print(
            "\n====================================================\n" +
                    "Reference Position: ${pvtInputData.refLocation.llaLocation.latitude}," +
                    " ${pvtInputData.refLocation.llaLocation.longitude}\n"
        )
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
