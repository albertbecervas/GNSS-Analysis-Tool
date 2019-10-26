package com.abecerra.pvt_computation.domain.computation.algorithm

import com.abecerra.pvt_computation.data.input.PvtInputData
import com.abecerra.pvt_computation.data.input.mapper.PvtAlgorithmInputDataMapper
import com.abecerra.pvt_computation.data.output.PvtLatLng
import com.abecerra.pvt_computation.data.output.PvtOutputData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.junit.Before
import org.junit.Test

class PvtAlgorithmImplTest {

    private lateinit var pvtAlgorithm: PvtAlgorithm

    private lateinit var pvtInputData: PvtInputData

    @Before
    fun setUp() {
        pvtAlgorithm = PvtAlgorithmImpl()
        pvtInputData = getPvtInputDataFromFile("input/test.txt")
    }

    @Test
    fun executePvtComputationAlgorithm() {
        pvtInputData.computationSettings.forEach {
            val outputData = pvtAlgorithm.executePvtAlgorithm(
                PvtAlgorithmInputDataMapper.mapFromPvtInputData(pvtInputData, it)
            )
            val location = outputData?.pvtFix?.pvtLatLng
            printOutput(location)
            assert(outputData != null)
        }
    }

    private fun printOutput(location: PvtLatLng?) {
        print("\n\n")
        println("Location: latitude,longitude --> ${location?.lat ?: "-,"},${location?.lng ?: "-,"}")
        print("\n\n")
    }

    private fun getPvtInputDataFromFile(fileName: String): PvtInputData {

        //Getting the file
        val fileInput = this.javaClass.classLoader?.getResourceAsStream(fileName)
        val pvtInputDataString = fileInput?.bufferedReader().use { it?.readText() }
        val type = object : TypeToken<PvtInputData>() {}.type

        //parse data into the specified object
        return Gson().fromJson<PvtInputData>(pvtInputDataString, type)
    }
}
