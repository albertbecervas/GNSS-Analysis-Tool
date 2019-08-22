package com.abecerra.gnssanalysis.core.navigator

interface Navigator {

    fun navigateToMainActivity()

    fun navigateToComputationSettingsActivity()

    fun sendEmail(to: String)

}