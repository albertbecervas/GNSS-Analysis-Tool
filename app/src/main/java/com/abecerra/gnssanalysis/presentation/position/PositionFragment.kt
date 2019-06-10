package com.abecerra.gnssanalysis.presentation.position


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.abecerra.gnssanalysis.R
import com.abecerra.gnssanalysis.core.base.BaseLocationFragment
import com.abecerra.gnssanalysis.core.computation.data.PvtResponse
import com.abecerra.gnssanalysis.core.utils.extensions.showStopAlert
import com.abecerra.gnssanalysis.presentation.map.MapFragment
import kotlinx.android.synthetic.main.fragment_position.*

class PositionFragment : BaseLocationFragment(), MapFragment.MapListener {

//    private val viewModel: PositionViewModel by viewModel()

    private var mapFragment: MapFragment = MapFragment()

    private var isStartedComputing = false

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


    override fun onPvtResult(pvtResponse: PvtResponse?) {

    }

    override fun onMapGesture() {
    }

}
