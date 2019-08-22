package com.abecerra.gnssanalysis.core.computation.repository

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import timber.log.Timber
import java.io.File

class PvtRepositoryImpl : PvtRepository {

    override fun uploadNmeaFile(file: File) {

        val ref = FirebaseStorage.getInstance().reference

        val fileUri = Uri.fromFile(File(file.absolutePath))
        val fileRef = ref.child("$NMEA_REF${fileUri?.lastPathSegment ?: "Last Unnamed Ref"}")
        val uploadTask = fileRef.putFile(fileUri)

        uploadTask.addOnSuccessListener {
            Timber.d("success")
        }.addOnFailureListener {
            Timber.d("error")
        }

    }

    companion object {
        const val NMEA_REF = "nmea/"
    }

}