package com.abecerra.gnssanalysis.presentation.ui.splash

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.abecerra.gnssanalysis.R
import com.abecerra.gnssanalysis.core.base.BaseActivity
import com.abecerra.gnssanalysis.core.utils.extensions.PERMISSION_ACCESS_FINE_LOCATION
import com.abecerra.gnssanalysis.core.utils.extensions.PERMISSION_READ_EXTERNAL_STORAGE
import com.abecerra.gnssanalysis.core.utils.extensions.PERMISSION_WRITE_EXTERNAL_STORAGE
import com.abecerra.gnssanalysis.core.utils.extensions.checkPermissionsList
import com.abecerra.gnssanalysis.core.utils.extensions.requestPermissionss

class SplashActivity : BaseActivity() {

    private var isComingFromSettings = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            if (checkPermissionsList(
                    arrayOf(
                        PERMISSION_WRITE_EXTERNAL_STORAGE,
                        PERMISSION_READ_EXTERNAL_STORAGE, PERMISSION_ACCESS_FINE_LOCATION
                    )
                )
            ) {
                goToMainActivity()
            } else {
                requestPermissionss(
                    arrayOf(
                        PERMISSION_ACCESS_FINE_LOCATION,
                        PERMISSION_WRITE_EXTERNAL_STORAGE, PERMISSION_READ_EXTERNAL_STORAGE
                    ), PERMISSIONS_CODE
                )
            }
        }, 1000L)
    }

    override fun onResume() {
        super.onResume()
        if (isComingFromSettings) {
            Handler().postDelayed({

                if (checkPermissionsList(
                        arrayOf(
                            PERMISSION_WRITE_EXTERNAL_STORAGE,
                            PERMISSION_READ_EXTERNAL_STORAGE, PERMISSION_ACCESS_FINE_LOCATION
                        )
                    )
                ) {
                    goToMainActivity()
                } else {
                    requestPermissionss(
                        arrayOf(
                            PERMISSION_ACCESS_FINE_LOCATION,
                            PERMISSION_WRITE_EXTERNAL_STORAGE, PERMISSION_READ_EXTERNAL_STORAGE
                        ), PERMISSIONS_CODE
                    )
                }
            }, TIME_OUT)
        }
    }

    private fun goToMainActivity() {
        navigator.navigateToMainActivity()
        finish()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (checkPermissionsList(
                    arrayOf(
                        PERMISSION_WRITE_EXTERNAL_STORAGE,
                        PERMISSION_READ_EXTERNAL_STORAGE, PERMISSION_ACCESS_FINE_LOCATION
                    )
                )
            ) {
                goToMainActivity()
            } else {
                val showRationale =
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                if (!showRationale) {
                    //"never ask again" box checked

                    val dialog = AlertDialog.Builder(this)
                        .setTitle("Turn on the permissions to proceed")
                        .setMessage("In order to activate the permissions, go to settings...")
                        .setCancelable(false)
                        .setPositiveButton("OK") { _, _ ->
                            goToSettings()
                        }
                        .create()

                    dialog.show()
                }
            }
        } else {

            val showRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            if (!showRationale) {
                //"never ask again" box checked

                val dialog = AlertDialog.Builder(this)
                    .setTitle("Turn on the permissions to proceed")
                    .setMessage("In order to activate the permissions, go to settings...")
                    .setCancelable(false)
                    .setPositiveButton("OK") { _, _ ->
                        goToSettings()
                    }
                    .create()

                dialog.show()
            } else {

                val dialog = AlertDialog.Builder(this)
                    .setTitle("Permissions required")
                    .setMessage("In order to use the application, the location permissions must be granted")
                    .setCancelable(false)
                    .setPositiveButton("OK") { _, _ ->
                        requestPermissionss(
                            arrayOf(
                                PERMISSION_ACCESS_FINE_LOCATION,
                                PERMISSION_WRITE_EXTERNAL_STORAGE, PERMISSION_READ_EXTERNAL_STORAGE
                            ), PERMISSIONS_CODE
                        )
                    }
                    .create()

                dialog.show()
            }
        }
    }

    private fun goToSettings() {
        val myAppSettings = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName"))
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT)
        myAppSettings.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(myAppSettings)
        isComingFromSettings = true
    }

    companion object {
        const val PERMISSIONS_CODE = 99
        private const val TIME_OUT = 2000L
    }
}
