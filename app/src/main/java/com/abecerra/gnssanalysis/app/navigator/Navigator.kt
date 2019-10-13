package com.abecerra.gnssanalysis.app.navigator

import androidx.fragment.app.Fragment

interface Navigator {

    fun navigateToMainActivity()

    fun navigateToComputationSettingsActivity(fragment: Fragment? = null)

    fun sendEmail(to: String)
}
