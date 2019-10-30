package com.abecerra.pvt_computation.domain.computation.algorithm

import com.abecerra.pvt_computation.data.input.PvtInputData
import com.abecerra.pvt_computation.data.input.mapper.PvtAlgorithmInputDataMapper
import com.abecerra.pvt_computation.data.output.PvtLatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.junit.Before
import org.junit.Test

class PvtAlgorithmImplTest {

    private lateinit var pvtAlgorithm: PvtAlgorithm

    private lateinit var pvtInputDataList: ArrayList<PvtInputData>

    @Before
    fun setUp() {
        pvtAlgorithm = PvtAlgorithmImpl()
        pvtInputDataList = arrayListOf()
        repeat(40) {
            pvtInputDataList.add(getPvtInputDataFromFile("input/input_${it + 1}.txt"))
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

    private fun printOutput(location: PvtLatLng?) {
        print("Computed Position:${location?.lat},${location?.lng}")
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
