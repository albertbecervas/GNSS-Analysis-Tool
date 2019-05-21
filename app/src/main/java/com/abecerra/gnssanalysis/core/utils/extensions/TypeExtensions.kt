package com.abecerra.gnssanalysis.core.utils.extensions

import android.graphics.Bitmap
import android.support.v4.widget.TextViewCompat
import android.widget.TextView
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*

fun Any?.toString(): String {
    return this?.toString() ?: ""
}

fun Any?.toInt(): Int {
    return this?.let {
        when (it) {
            is String -> it.toIntOrNull() ?: 0
            is Int -> it
            is Double -> it.toInt()
            is Float -> it.toInt()
            is Boolean -> if (it) 1 else 0
            else -> 0
        }
    } ?: 0
}

fun Any?.toFloat(): Float {
    return this?.let {
        when (it) {
            is String -> it.toFloatOrNull() ?: 0f
            is Int -> it.toFloat()
            is Double -> it.toFloat()
            is Float -> it
            is Boolean -> if (it) 1f else 0f
            else -> 0f
        }
    } ?: 0f
}

fun Any?.toDouble(): Double {
    return this?.let {
        when (it) {
            is String -> it.toDoubleOrNull() ?: 0.0
            is Int -> it.toDouble()
            is Double -> it
            is Float -> it.toDouble()
            is Boolean -> if (it) 1.0 else 0.0
            else -> 0.0
        }
    } ?: 0.0
}

fun Any?.toBoolean(): Boolean {
    return this?.let {
        when (it) {
            is String -> it.toLowerCase() == "true"
            is Int -> it == 1
            is Double -> it == 1.0
            is Float -> it == 1.0f
            is Boolean -> it
            else -> false
        }
    } ?: false
}

fun Bitmap?.toFile(): File? {
    if (this != null) {
        try {
            val tempFile = File.createTempFile(UUID.randomUUID().toString(), ".jpg")

            val imageBytes = ByteArrayOutputStream().use {
                compress(Bitmap.CompressFormat.JPEG, 100, it)
                it.toByteArray()
            }

            tempFile.writeBytes(imageBytes)
            return tempFile
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    return null
}

fun <K> List<K>?.toList(): List<K> {
    return this?.let {
        if (it.isNotEmpty()) it
        else arrayListOf()
    } ?: arrayListOf()
}

fun <K> ArrayList<K>.replace(oldItem: K?, newItem: K?) {
    oldItem?.let {
        val itemPosition = indexOf(oldItem)
        removeAt(itemPosition)
        newItem?.let {
            add(itemPosition, newItem)
        }
    }
}

fun <K> List<K?>.filterNotNullList(): List<K> {
    val list = arrayListOf<K>()
    this.filter {
        it?.let {
            list.add(it)
            true
        } ?: false
    }
    return list
}

fun File.toRequestBodyImage(partName: String): MultipartBody.Part {
    val requestFile = RequestBody.create(MediaType.parse("image/jpg"), this)
    return MultipartBody.Part.createFormData(partName, name, requestFile)
}

fun TextView.setAutoResize() =
    TextViewCompat.setAutoSizeTextTypeWithDefaults(this, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM)

fun String.beautify(): String {
    val stringBuilder = StringBuilder()

    map {
        if (!it.isLetter()) {
            ' '
        } else {
            it
        }
    }.dropLastWhile {
        it == ' '
    }.dropWhile {
        it == ' '
    }.partition { char ->
        char.isLetter() || char.isWhitespace()
    }.first.forEach {
        stringBuilder.append(it)
    }

    return stringBuilder.toString().toLowerCase().capitalize()
}


fun String.isWhitespace() = all { it.isWhitespace() }
