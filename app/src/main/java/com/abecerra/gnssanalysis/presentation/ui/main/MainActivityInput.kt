package com.abecerra.gnssanalysis.presentation.ui.main

import com.abecerra.gnssanalysis.core.computation.GnssService

/**
 * This MainActivityInput interface is the protocol to communicate with MainActivity
 */
interface MainActivityInput {

    /**
     * This getGnssService function returns the GnssService instance in MainActivity to let child fragments to
     * communicate with it.
     *
     * @return GnssService nullable instance
     */
    fun getGnssService(): GnssService?
}
