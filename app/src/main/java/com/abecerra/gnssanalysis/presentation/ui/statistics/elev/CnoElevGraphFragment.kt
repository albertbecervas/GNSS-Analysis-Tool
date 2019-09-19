package com.abecerra.gnssanalysis.presentation.ui.statistics.elev

import android.graphics.Color
import android.location.GnssStatus
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.abecerra.gnssanalysis.R
import com.abecerra.gnssanalysis.presentation.ui.statistics.BaseGraphFragment
import com.abecerra.pvt.computation.utils.Constants
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_graph.tabLayout
import kotlinx.android.synthetic.main.view_graph.rlGraph
import kotlinx.android.synthetic.main.view_graph.xAxisTitle
import kotlinx.android.synthetic.main.view_graph.yAxisTitle

class CnoElevGraphFragment : BaseGraphFragment(), CnoElevGraphFragmentInput {

    private var cnoElevGraph: CnoElevGraph? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTabLayout()
    }

    override fun onResume() {
        super.onResume()
        tabLayout.getTabAt(0)?.select()
    }

    override fun setGraph(view: View) {
        cnoElevGraph = CnoElevGraph(view.context)
        rlGraph?.addView(cnoElevGraph)
        yAxisTitle?.text = view.context.getString(R.string.cnoAxisTitle)
        xAxisTitle?.text = view.context.getString(R.string.elevAxisTitle)
    }

    private fun setTabLayout() {
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
                    cnoElevGraph?.updateBand(band)
                }
            }
        })
    }

    override fun getGraphInformation(): String {
        return getString(R.string.elev_cno_information_detail)
    }

    override fun onGnssStatusReceived(gnssStatus: GnssStatus) {
        cnoElevGraph?.plotElevCNoGraph(gnssStatus)
    }

    private fun createTab(title: String): TabLayout.Tab {
        val tab = tabLayout.newTab().setCustomView(R.layout.item_filter_tab)
        tab.customView?.findViewById<TextView>(R.id.tvFilterTitle)?.text = title
        return tab
    }
}
