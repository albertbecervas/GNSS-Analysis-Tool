package com.abecerra.pvt_acquisition.app.utils

import com.abecerra.pvt_acquisition.data.inari.GnssData
import com.abecerra.pvt_acquisition.data.inari.getAcqInfo
import com.abecerra.pvt_computation.data.input.PvtInputData
import com.google.gson.Gson
import java.io.*

object Logger {

    fun savePvtInputData(name: String, pvtInputData: PvtInputData) {
        val pvtInputDataJson = Gson().toJson(pvtInputData)

        val directoryName = "GNSSTool/input/"

        val fileDir =
            "/storage/emulated/0/Android/data/com.abecerra.gnssanalysis/files/$directoryName"

        writeToFile(fileDir, name, pvtInputDataJson)
    }

    fun saveGnssDataForInariComparison(name: String, gnssData: GnssData) {
        val acqInformation = getAcqInfo(gnssData)
        val pvtInputDataJson = Gson().toJson(acqInformation)

        val directoryName = "GNSSTool/inari/"

        val fileDir =
            "/storage/emulated/0/Android/data/com.abecerra.gnssanalysis/files/$directoryName"

        writeToFile(fileDir, name, pvtInputDataJson)
    }

    private fun writeToFile(fileDir: String, fileName: String, fileContent: String) {
        Thread(Runnable {
            try {
                val dir = File(fileDir)
                dir.mkdirs()

                val file = File(dir, fileName)
                file.setReadable(true, false)

                var inputStream: InputStream? = null
                var outputStream: OutputStream? = null

                try {
                    val fileReader = ByteArray(4096)

                    inputStream = fileContent.byteInputStream()
                    outputStream = FileOutputStream(file)

                    while (true) {
                        val read = inputStream.read(fileReader)

                        if (read == -1) {
                            break
                        }
                        outputStream.write(fileReader, 0, read)
                    }
                    outputStream.flush()

                } catch (e: IOException) {
                } finally {
                    inputStream?.close()
                    outputStream?.close()
                }
            } catch (e: Exception) {

            }
        }).start()
    }
}
