package com.abecerra.gnssanalysis.presentation.ui.position


import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.abecerra.gnssanalysis.R
import com.abecerra.gnssanalysis.core.base.BaseFragment
import com.abecerra.gnssanalysis.core.utils.extensions.Data
import com.abecerra.gnssanalysis.core.utils.extensions.DataState.*
import com.abecerra.gnssanalysis.core.utils.extensions.observe
import com.abecerra.gnssanalysis.core.utils.extensions.showSelectedComputationSettingsAlert
import com.abecerra.gnssanalysis.core.utils.extensions.showStopAlert
import com.abecerra.gnssanalysis.presentation.ui.main.MainActivityInput
import com.abecerra.gnssanalysis.presentation.ui.map.MapFragment
import com.abecerra.gnssanalysis.presentation.ui.position.PvtComputationViewModel.Status.*
import com.abecerra.pvt.computation.data.ComputedPvtData
import kotlinx.android.synthetic.main.fragment_position.*
import org.koin.android.viewmodel.ext.android.viewModel

class PvtComputationFragment : BaseFragment(), MapFragment.MapListener {

    private val viewModel: PvtComputationViewModel by viewModel()

    private var mActivityListener: MainActivityInput? = null

    private var mapFragment: MapFragment = MapFragment()

    private var legendAdapter = LegendAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(viewModel) {
            observe(pvt, ::updatePvt)
            observe(computeButtonText, ::updateComputationButtonText)
            observe(status, ::updateStatus)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_position, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mActivityListener = context as? MainActivityInput
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViews()
    }

    private fun setViews() {

        replaceFragment(R.id.mapFragmentContainer, mapFragment)

        btComputeAction.setOnClickListener {
            if (viewModel.isComputing()) stopComputing() else viewModel.startComputingIfSelectedSettings()
        }

        fabOptions.setOnClickListener {
            navigator.navigateToComputationSettingsActivity(this)
        }

        btRecenter.setOnClickListener {
            viewModel.setIsCameraIntercepted(false)
            btRecenter.visibility = View.GONE
        }

        ivLegendArrow.setOnClickListener {
            cvLegend.visibility = if (cvLegend.visibility == View.GONE) View.VISIBLE else View.GONE
            ivLegendArrow.rotation = ivLegendArrow.rotation + 180
        }

        rvLegend.layoutManager = LinearLayoutManager(context)
        rvLegend.adapter = legendAdapter

    }

    override fun onResume() {
        super.onResume()
        setLegendItems()
    }

    private fun setLegendItems() {
        val legendItems = mPrefs.getSelectedComputationSettingsList()
        if (legendItems.isNotEmpty()) {
            legendAdapter.setItems(legendItems)
            cvLegendArrow.visibility = View.VISIBLE
            clLegend.visibility = View.VISIBLE
        } else {
            legendAdapter.clear()
            cvLegendArrow.visibility = View.GONE
            clLegend.visibility = View.GONE
        }
    }

    private fun updatePvt(data: Data<List<ComputedPvtData>>?) {
        data?.let {
            when (it.dataState) {
                LOADING -> {
                }
                SUCCESS -> {
                    addPvtToMap(it)
                }
                ERROR -> {
                }
            }
        }
    }

    private fun updateComputationButtonText(text: Data<String>?) {
        btComputeAction.text = text?.data ?: getString(R.string.start_computing)
    }

    private fun updateStatus(status: PvtComputationViewModel.Status?) {
        status?.let {
            when (it) {
                STARTED_COMPUTING -> startComputing()
                STOPPED_COMPUTING -> stopComputing()
                SHOW_LOADING -> showMapLoading()
                HIDE_LOADING -> hideMapLoading()
                ERROR_EPH -> {
                }
                ERROR_COMPUTING -> {
                }
                NONE_PARAM_SELECTED -> {
                    context?.showSelectedComputationSettingsAlert {
                        navigator.navigateToComputationSettingsActivity(this)
                    }
                }
            }
        }
    }

    private fun startComputing() {
        showMapLoading()
        mActivityListener?.getGnssService()?.let {
            it.startComputing(mPrefs.getSelectedComputationSettingsList())
            it.bindPvtListener(viewModel)
        }
    }

    private fun stopComputing() {
        context?.showStopAlert {
            viewModel.stopComputing()
            hideMapLoading()
            btRecenter.visibility = View.GONE
            mActivityListener?.getGnssService()?.let {
                it.stopComputing()
                it.unbindPvtListener(viewModel)
            }
        }
    }

    private fun addPvtToMap(it: Data<List<ComputedPvtData>>) {
        it.data?.let { pos ->
            pos.forEach { resp -> mapFragment.addMarkerFromPvtResponse(resp) }
            if (pos.isNotEmpty()) mapFragment.updateCamera(pos[0], viewModel.isCameraIntercepted())
        }
    }

    private fun showMapLoading() {
        pbMap?.visibility = View.VISIBLE
        mapFragment.clearMap()
    }

    private fun hideMapLoading() {
        pbMap?.visibility = View.GONE
    }

    override fun onMapGesture() {
        if (btComputeAction.text == getString(R.string.stop_computing)) {
            viewModel.setIsCameraIntercepted(true)
            btRecenter.visibility = View.VISIBLE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            SETTINGS_CODE -> {
                if (resultCode == RESULT_OK) {
                    viewModel.startComputingIfSelectedSettings()
                }
            }
        }
    }

    companion object {
        const val SETTINGS_CODE: Int = 0
    }

}
