package com.abecerra.gnssanalysis.core.computation.repository

import java.io.File

interface PvtRepository {

    fun uploadNmeaFile(file: File)

}