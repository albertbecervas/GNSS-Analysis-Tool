package com.abecerra.pvt_computation.utils.parsers

import com.abecerra.pvt_computation.data.input.PvtInputData
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import jdk.nashorn.internal.parser.JSONParser

class PvtInputJsonDataParser(private val parser: JSONParser) : PvtInputDataParser<String>() {


    override fun String.parse(): PvtInputData {

        val jsonObject = JsonParser().parse(this).asJsonObject
        jsonObject.asJsonObject[""].asString

        return PvtInputData(
            cn0mask = jsonObject.getCnoMask()
        )
    }

    private fun JsonObject.getCnoMask(): Int{
        return this["cnoMask"].asInt
    }
}
