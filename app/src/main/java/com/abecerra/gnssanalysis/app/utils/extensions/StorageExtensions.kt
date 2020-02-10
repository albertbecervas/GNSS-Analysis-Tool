package com.abecerra.gnssanalysis.app.utils.extensions

import android.annotation.SuppressLint
import android.os.Environment
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

private val root: File =
    android.os.Environment.getExternalStorageDirectory()

private const val APP_ROOT: String = "/Innroute/Logs/"

@SuppressLint("SetWorldReadable")
fun saveFile(
    url: String,
    responseBody: ResponseBody
) {
    try {

        val dir =
            File(root.absolutePath + APP_ROOT)
        dir.mkdirs()

        val file = File(dir, url)
        file.setReadable(true, false)

        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null

        try {
            val fileReader = ByteArray(4096)

            inputStream = responseBody.byteStream()
            outputStream = FileOutputStream(file)

            while (true) {
                val read = inputStream!!.read(fileReader)

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
    } catch (e: IOException) {
    }
}

fun getFilesList(): Array<File> {
    val path = Environment.getExternalStorageDirectory().toString() + APP_ROOT
    val directory = File(path)
    return directory.listFiles()
}

class Logger(fileName: String = "Location.txt") {

    private var logFile: File? = null

    init {
//        val dir =
//                File(root.absolutePath + APP_ROOT)
//        dir.mkdirs()
//        logFile = File(dir, fileName)
    }

    fun log(s: String) {
//        val date = Date()
//        val formattedDate = SimpleDateFormat("h:mm:ss a dd-MM-yyyy", Locale.ENGLISH).format(date)
//
//        try {
//            val fw = FileWriter(this.logFile, true)
//            fw.write("$formattedDate ---> $s")
//            fw.write(System.lineSeparator())
//            fw.close()
//        } catch (ex: IOException) {
//            System.err.println("$formattedDate ---> Couldn't log this: $s")
//        }
    }
}
