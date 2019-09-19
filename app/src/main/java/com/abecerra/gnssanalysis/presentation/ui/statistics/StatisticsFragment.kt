package com.abecerra.gnssanalysis.presentation.ui.statistics

import android.hardware.SensorEvent
import android.location.GnssMeasurementsEvent
import android.location.GnssStatus
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.abecerra.gnssanalysis.R
import com.abecerra.gnssanalysis.core.base.BaseFragment
import com.abecerra.gnssanalysis.core.computation.GnssServiceOutput
import com.abecerra.gnssanalysis.core.utils.CustomFragmentPagerAdapter
import com.abecerra.gnssanalysis.presentation.ui.statistics.agc.AgcCnoFragmentInput
import com.abecerra.gnssanalysis.presentation.ui.statistics.agc.AgcCnoGraphFragment
import com.abecerra.gnssanalysis.presentation.ui.statistics.elev.CnoElevGraphFragment
import com.abecerra.gnssanalysis.presentation.ui.statistics.elev.CnoElevGraphFragmentInput
import kotlinx.android.synthetic.main.fragment_statistics.graphTitleSpinner
import kotlinx.android.synthetic.main.fragment_statistics.graphsVp
import kotlinx.android.synthetic.main.fragment_statistics.ivGraphInformation
import kotlinx.android.synthetic.main.fragment_statistics.tvInformationDetail
import kotlinx.android.synthetic.main.fragment_statistics.tvInformationTitle

class StatisticsFragment : BaseFragment(), GnssServiceOutput.GnssEventsListener {

    private val graphs = arrayListOf<Pair<String, BaseGraphFragment>>()

    private var agcCnoFragmentInput: AgcCnoFragmentInput? = null
    private var cnoElevGraphFragmentInput: CnoElevGraphFragmentInput? = null

    private var titlesAdapter: GraphsTitleSpinnerAdapter? = null

    private fun setGraphs() {
        val agcCnoGraphFragment = AgcCnoGraphFragment()
        val cnoElevGraphFragment = CnoElevGraphFragment()
        agcCnoFragmentInput = agcCnoGraphFragment
        cnoElevGraphFragmentInput = cnoElevGraphFragment
        graphs.add(Pair(getString(R.string.agc_cno_graph_name), agcCnoGraphFragment))
        graphs.add(Pair(getString(R.string.cno_elev_graph_name), cnoElevGraphFragment))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_statistics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setGraphs()
        setViews(view)
    }

    private fun setViews(view: View) {
        val adapter = CustomFragmentPagerAdapter<BaseGraphFragment>(childFragmentManager)
        graphs.forEach {
            adapter.addFragment(it.second, it.first)
        }
        graphsVp.adapter = adapter
        graphsVp.setPagingEnabled(true)

        graphsVp.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int, positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                tvInformationDetail.text = graphs[position].second.getGraphInformation()
                graphTitleSpinner.setSelection(position, true)
            }

        })

        titlesAdapter = GraphsTitleSpinnerAdapter(
            view.context,
            R.layout.item_graphs_spinner,
            R.id.graphTitleTv,
            graphs.map { it.first }
        )

        graphTitleSpinner.adapter = titlesAdapter

        tvInformationTitle.setOnClickListener {
            if (tvInformationDetail.visibility == View.VISIBLE) {
                tvInformationDetail.visibility = View.GONE
            } else {
                tvInformationDetail.visibility = View.VISIBLE
            }
            ivGraphInformation.rotation = ivGraphInformation.rotation + 180
        }
    }

    override fun onGnssStarted() {
    }

    override fun onGnssStopped() {
    }

    override fun onSatelliteStatusChanged(status: GnssStatus) {
        cnoElevGraphFragmentInput?.onGnssStatusReceived(status)
    }

    override fun onGnssMeasurementsReceived(event: GnssMeasurementsEvent) {
        agcCnoFragmentInput?.onGnssMeasurementReceived(event.measurements)
    }

    override fun onSensorEvent(event: SensorEvent) {
    }

    override fun onNmeaMessageReceived(message: String, timestamp: Long) {
    }

    override fun onLocationReceived(location: Location) {
    }
}
