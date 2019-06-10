package com.abecerra.pvt

import com.abecerra.pvt.computation.PvtEngine
import com.abecerra.pvt.computation.data.GnssData
import org.junit.Test

class PvtEngineTest {

    @Test
    fun testPVTEngine() {

        val gnssData = GnssData()

        PvtEngine.computePosition(gnssData)
            .subscribe({
                print(it)
            }, {
                print(it.message)
            })

    }

}
