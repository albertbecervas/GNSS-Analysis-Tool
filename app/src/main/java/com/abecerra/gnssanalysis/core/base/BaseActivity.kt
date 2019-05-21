package com.abecerra.gnssanalysis.core.base

import android.support.v7.app.AppCompatActivity
import com.abecerra.gnssanalysis.core.navigator.ActivityNavigator
import com.abecerra.gnssanalysis.core.utils.AppSharedPreferences
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

abstract class BaseActivity : AppCompatActivity() {
    protected val navigator: ActivityNavigator by inject { parametersOf(this) }
    protected val mPrefs: AppSharedPreferences by inject()

    companion object {
        const val PICK_FROM_CAMERA = 1438
        const val PICK_FROM_GALLERY = 1439
    }
}
