package com.abecerra.gnssanalysis.presentation.ui.statistics.agc

import android.graphics.Color
import android.location.GnssMeasurement
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.abecerra.gnssanalysis.R
import com.abecerra.gnssanalysis.presentation.ui.statistics.BaseGraphFragment
import com.abecerra.gnssanalysis.presentation.ui.statistics.L1_E1_text
import com.abecerra.gnssanalysis.presentation.ui.statistics.L5_E5_text
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_graph.tabLayout
import kotlinx.android.synthetic.main.view_graph.rlGraph
import kotlinx.android.synthetic.main.view_graph.xAxisTitle
import kotlinx.android.synthetic.main.view_graph.yAxisTitle

class AgcCnoGraphFragment : BaseGraphFragment(), AgcCnoFragmentInput {

    private var agcCnoGraph: AgcCnoGraph? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTabLayout()
    }

    override fun onResume() {
        super.onResume()
        tabLayout.getTabAt(0)?.select()
    }

    override fun setGraph(view: View) {
        agcCnoGraph = AgcCnoGraph(view.context)
        yAxisTitle?.text = view.context.getString(R.string.agcAxisTitle)
        xAxisTitle?.text = view.context.getString(R.string.cnoAxisTitle)
        rlGraph?.addView(agcCnoGraph)
    }

    private fun setTabLayout() {
        tabLayout.setSelectedTabIndicatorColor(Color.TRANSPARENT)
        tabLayout.addTab(createTab(L1_E1_text))
        tabLayout.addTab(createTab(L5_E5_text))

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
                    agcCnoGraph?.updateBand(band)
                }
            }
        })
    }

    override fun getGraphInformation(): String {
        return getString(R.string.agc_cno_information_detail)
    }

    override fun onGnssMeasurementReceived(gnssMeasurements: Collection<GnssMeasurement>) {
        agcCnoGraph?.plotAgcCNoGraph(gnssMeasurements)
    }

    private fun createTab(title: String): TabLayout.Tab {
        val tab = tabLayout.newTab().setCustomView(R.layout.item_filter_tab)
        tab.customView?.findViewById<TextView>(R.id.tvFilterTitle)?.text = title
        return tab
    }
}
