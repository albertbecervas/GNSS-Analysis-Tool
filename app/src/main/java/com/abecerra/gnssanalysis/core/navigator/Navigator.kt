package com.abecerra.gnssanalysis.core.navigator

import androidx.fragment.app.Fragment

interface Navigator {

    fun navigateToMainActivity()

    fun navigateToComputationSettingsActivity(fragment: Fragment? = null)

    fun sendEmail(to: String)

}