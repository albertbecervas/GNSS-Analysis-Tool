package com.abecerra.gnssanalysis.presentation.ui.position


import android.hardware.SensorEvent
import android.location.GnssMeasurementsEvent
import android.location.GnssStatus
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.abecerra.gnssanalysis.R
import com.abecerra.gnssanalysis.core.base.BaseGnssFragment
import com.abecerra.gnssanalysis.core.computation.GnssService
import com.abecerra.gnssanalysis.core.computation.data.PvtResponse
import com.abecerra.gnssanalysis.core.utils.extensions.showStopAlert
import com.abecerra.gnssanalysis.presentation.ui.map.MapFragment
import kotlinx.android.synthetic.main.fragment_position.*

class PositionFragment : BaseGnssFragment(), MapFragment.MapListener, GnssService.GnssServiceOutput.GnssEventsListener,
    GnssService.GnssServiceOutput.PvtListener {
//    private val viewModel: PositionViewModel by viewModel()

    private var mapFragment: MapFragment = MapFragment()

    private var isStartedComputing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onServiceConnected() {
        mService?.bindGnssEventsListener(this)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_position, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViews()
    }

    private fun setViews() {

        btComputeAction.setOnClickListener {
            if (isStartedComputing) {
                onStopComputing()
            } else {
                onStartComputing()
            }
        }

        childFragmentManager.beginTransaction()
            .replace(R.id.mapFragmentContainer, mapFragment)
            .commit()

    }


    private fun onStartComputing() {
        val selectedModes = mPrefs.getSelectedModesList()

//        if (selectedModes.isEmpty()) { // If no constellation or band has been selected
//            context?.showSelectedComputationSettingsAlert {
//                //navigate to settings
//            }
//        } else {
        showMapLoading()
        mapFragment.clearMap()
        isStartedComputing = true
        btComputeAction.text = getString(R.string.stop_computing)
        startComputing(selectedModes)
//        }

    }

    private fun onStopComputing() {
        context?.showStopAlert {
            btComputeAction.text = getString(R.string.start_computing)
            hideMapLoading()
            btRecenter.visibility = View.GONE
            stopComputing()
        }
    }

    fun showMapLoading() {
        pbMap?.visibility = View.VISIBLE
    }

    private fun hideMapLoading() {
        pbMap?.visibility = View.GONE
    }


    override fun onPvtResponse(pvtResponse: PvtResponse) {
        val s = ""

    }

    override fun onPvtError(error: String) {
        val s = ""

    }

    override fun onMapGesture() {
    }

    override fun onGnssStarted() {
        val s = ""
    }

    override fun onGnssStopped() {
        val s = ""
    }

    override fun onSatelliteStatusChanged(status: GnssStatus) {
        val s = ""
    }

    override fun onGnssMeasurementsReceived(event: GnssMeasurementsEvent) {
        val s = ""
    }

    override fun onSensorEvent(event: SensorEvent) {
        val s = ""
    }

    override fun onNmeaMessageReceived(message: String, timestamp: Long) {
        val s = ""
    }

    override fun onLocationReceived(location: Location) {
        val s = ""
    }


}
