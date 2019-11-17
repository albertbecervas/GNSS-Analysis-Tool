package com.abecerra.pvt_computation.domain.computation.algorithm.leastsquares.mock

import com.abecerra.pvt_computation.data.EcefLocation
import com.abecerra.pvt_computation.data.algorithm.LeastSquaresInputData

class LeastSquaresInputDataMock {

    lateinit var leastSquaresInputData: LeastSquaresInputData

    init {
        mockLeastSquaresInputData()
    }

    private fun mockLeastSquaresInputData() {
        leastSquaresInputData = LeastSquaresInputData()

        leastSquaresInputData.refPosition =
            EcefLocation(4780831.020981176, 177276.84169624432, 4204238.938520334)

        leastSquaresInputData.p.addAll(
            arrayListOf(
                675.491155128926,
                -747.1995438709855,
                171.36413784697652,
                -176.76258469372988,
                -607.4827192611992,
                -457.8916869163513,
                439.2312754839659,
                44.75207533314824,
                78.58857748284936
            )
        )

        leastSquaresInputData.a.add(
            arrayListOf(
                -0.8937522480755518,
                0.10297014969755006,
                0.4365822572335729,
                1.0,
                0.0
            )
        )
        leastSquaresInputData.a.add(
            arrayListOf(
                -0.7514932793217105,
                0.3984122666979429,
                0.5258569357524049,
                1.0,
                0.0
            )
        )
        leastSquaresInputData.a.add(
            arrayListOf(
                -0.7142392781285359,
                -0.3506015771171496,
                -0.605756376525576,
                1.0,
                0.0
            )
        )
        leastSquaresInputData.a.add(
            arrayListOf(
                -0.5886033329296652,
                0.25551673913292144,
                -0.7669793429336726,
                1.0,
                0.0
            )
        )
        leastSquaresInputData.a.add(
            arrayListOf(
                -0.5271086385073724,
                -0.7905270602770312,
                0.31180675133912544,
                1.0,
                0.0
            )
        )
        leastSquaresInputData.a.add(
            arrayListOf(
                -0.42503918880360614,
                0.8136567498542282,
                -0.39661616381310827,
                1.0,
                0.0
            )
        )
        leastSquaresInputData.a.add(
            arrayListOf(
                -0.1929354748343819,
                -0.5934582005135022,
                -0.78139827667695,
                1.0,
                0.0
            )
        )
        leastSquaresInputData.a.add(
            arrayListOf(
                -0.8017566735656292,
                0.07068962600248387,
                -0.59345531690988,
                1.0,
                1.0
            )
        )
        leastSquaresInputData.a.add(
            arrayListOf(
                -0.682458456972001,
                -0.1550602776760384,
                -0.7142875924964855,
                1.0,
                1.0
            )
        )

        leastSquaresInputData.cn0.addAll(
            arrayListOf(
                33.403160095214844,
                35.59198760986328,
                25.38457679748535,
                28.967432022094727,
                32.22984313964844,
                40.63890075683594,
                14.734972953796387,
                25.489418029785156,
                19.102436065673828
            )
        )

        leastSquaresInputData.isMultiC = true
    }
}
