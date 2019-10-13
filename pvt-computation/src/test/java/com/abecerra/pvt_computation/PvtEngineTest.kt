package com.abecerra.pvt_computation

import com.abecerra.pvt_computation.domain.computation.PvtEngine
import com.abecerra.pvt_computation.data.input.PvtInputData
import org.junit.Test

class PvtEngineTest {

    @Test
    fun testPVTEngine() {

        val gnssData = PvtInputData()

        PvtEngine.computePosition(gnssData)
            .subscribe({
                print(it)
            }, {
                print(it.message)
            })

    }

}
