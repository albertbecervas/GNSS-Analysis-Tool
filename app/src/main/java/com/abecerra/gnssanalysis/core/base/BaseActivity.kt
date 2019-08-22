package com.abecerra.gnssanalysis.core.base

import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import com.abecerra.gnssanalysis.core.navigator.Navigator
import com.abecerra.gnssanalysis.core.utils.AppSharedPreferences
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

abstract class BaseActivity : AppCompatActivity() {
    protected val navigator: Navigator by inject { parametersOf(this) }
    protected val mPrefs: AppSharedPreferences by inject()

    protected fun replaceFragment(fragmentContainer: Int, fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(fragmentContainer, fragment).commit()
    }

    companion object {
        const val PICK_FROM_CAMERA = 1438
        const val PICK_FROM_GALLERY = 1439
    }
}
