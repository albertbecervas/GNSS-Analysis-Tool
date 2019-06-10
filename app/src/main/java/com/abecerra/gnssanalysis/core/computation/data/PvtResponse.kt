package com.abecerra.gnssanalysis.core.computation.data

import com.abecerra.pvt.computation.data.PvtFix
import java.io.Serializable

data class PvtResponse(
    var pvtFix: PvtFix?
) : Serializable