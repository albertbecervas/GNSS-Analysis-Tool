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

    /**
     * This bindPvtListenerToGnssService binds a fragment implementing PvtListener to the GnssService
     *
     * @param listener PvtListener to receive Pvt callbacks
     */
    fun bindPvtListenerToGnssService(listener: GnssService.GnssServiceOutput.PvtListener)

    /**
     * This unbindPvtListenerToGnssService unbinds a fragment implementing PvtListener from the GnssService
     *
     * @param listener PvtListener to receive Pvt callbacks
     */
    fun unbindPvtListenerFromGnssService(listener: GnssService.GnssServiceOutput.PvtListener)

}
