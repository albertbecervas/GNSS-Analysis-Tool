package com.abecerra.pvt_computation.utils.parsers

import com.abecerra.pvt_computation.data.input.PvtInputData

abstract class PvtInputDataParser<T> {

    abstract fun T.parse(): PvtInputData?

}
