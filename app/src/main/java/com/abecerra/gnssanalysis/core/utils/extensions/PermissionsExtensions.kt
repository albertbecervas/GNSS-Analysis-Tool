package com.abecerra.gnssanalysis.core.utils.extensions

import android.Manifest
import android.app.Activity
import android.app.Service
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

const val REQUEST_LOCATION_PERMISSION = 99
const val PERMISSION_ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
const val PERMISSION_READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE
const val PERMISSION_WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE
const val PERMISSION_CAMERA = Manifest.permission.CAMERA

fun checkPermission(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
}

fun checkPermissionsList(permissions: Array<String>): Boolean {
    var allPermissionsGranted: Boolean = true
    permissions.forEach {
        if (!checkPermission(it)) {
            allPermissionsGranted = false
        }
    }
    return allPermissionsGranted
}

fun Activity.requestPermissionss(permissions: Array<String>, code: Int) {
    ActivityCompat.requestPermissions(this, permissions, code)
}

fun Service.requestPermissions(permissions: Array<String>, code: Int) {
}
