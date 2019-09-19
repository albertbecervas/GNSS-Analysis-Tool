package com.abecerra.gnssanalysis.presentation.ui.statistics.agc

import android.graphics.Color
import android.location.GnssMeasurement
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.abecerra.gnssanalysis.R
import com.abecerra.gnssanalysis.presentation.ui.statistics.BaseGraphFragment
import com.abecerra.pvt.computation.utils.Constants
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_agc_cno_graph.agcCnoGraphComponent
import kotlinx.android.synthetic.main.fragment_cno_elev_graph.tabLayout

class AgcCnoGraphFragment : BaseGraphFragment(), AgcCnoFragmentInput {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_agc_cno_graph, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tabLayout.setSelectedTabIndicatorColor(Color.TRANSPARENT)
        tabLayout.addTab(createTab(Constants.L1_E1_text))
        tabLayout.addTab(createTab(Constants.L5_E5_text))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {
                p0?.customView?.findViewById<ConstraintLayout>(R.id.tab)
                    ?.setBackgroundResource(R.drawable.bg_corners_blue)
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
                p0?.customView?.findViewById<ConstraintLayout>(R.id.tab)
                    ?.setBackgroundResource(R.drawable.bg_corners_gray)
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.customView?.findViewById<ConstraintLayout>(R.id.tab)
                    ?.setBackgroundResource(R.drawable.bg_corners_blue)
                tab?.position?.let { band ->
                    agcCnoGraphComponent.updateBand(band)
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        tabLayout.getTabAt(0)?.select()
    }

    override fun getGraphInformation(): String {
        return getString(R.string.agc_cno_information_detail)
    }

    override fun onGnssMeasurementReceived(gnssMeasurements: Collection<GnssMeasurement>) {
        agcCnoGraphComponent.plotAgcCNoGraph(gnssMeasurements)
    }

    private fun createTab(title: String): TabLayout.Tab {
        val tab = tabLayout.newTab().setCustomView(R.layout.item_filter_tab)
        tab.customView?.findViewById<TextView>(R.id.tvFilterTitle)?.text = title
        return tab
    }
}
