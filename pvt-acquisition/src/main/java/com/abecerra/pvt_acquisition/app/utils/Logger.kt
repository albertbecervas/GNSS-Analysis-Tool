package com.abecerra.pvt_acquisition.app.utils

import com.abecerra.pvt_computation.data.input.PvtInputData
import com.google.gson.Gson
import java.io.*

object Logger {

    fun savePvtInputData(name: String, pvtInputData: PvtInputData) {
        Thread(Runnable {
            try {
                val pvtInputDataJson = Gson().toJson(pvtInputData)


                val directoryName = "GNSSTool/input/"

                val dir =
                    File("/storage/emulated/0/Android/data/com.abecerra.gnssanalysis/files/$directoryName")
                dir.mkdirs()

                val file = File(dir, name)
                file.setReadable(true, false)

                var inputStream: InputStream? = null
                var outputStream: OutputStream? = null

                try {
                    val fileReader = ByteArray(4096)

                    inputStream = pvtInputDataJson.byteInputStream()
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
