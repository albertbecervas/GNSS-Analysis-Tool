package com.abecerra.pvt_computation

import com.abecerra.pvt_computation.domain.computation.PvtComputationInteractorImpl
import com.abecerra.pvt_computation.data.input.PvtInputData
import org.junit.Test

class PvtComputationInteractorTest {

    @Test
    fun testPVTEngine() {

        val gnssData = PvtInputData()

        PvtComputationInteractorImpl.computePosition(gnssData)
            .subscribe({
                print(it)
            }, {
                print(it.message)
            })

    }

}
