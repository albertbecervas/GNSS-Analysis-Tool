package com.abecerra.gnssanalysis.presentation.ui.statistics.elev

import android.graphics.Color
import android.location.GnssStatus
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.abecerra.gnssanalysis.R
import com.abecerra.gnssanalysis.presentation.ui.statistics.BaseGraphFragment
import com.abecerra.gnssanalysis.presentation.ui.statistics.L1_E1_text
import com.abecerra.gnssanalysis.presentation.ui.statistics.L5_E5_text
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_cno_elev_graph.*

class CnoElevGraphFragment : BaseGraphFragment(), CnoElevGraphFragmentInput {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cno_elev_graph, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
                    cnoElevGraphComponent.updateBand(band)
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        tabLayout.getTabAt(0)?.select()
    }

    override fun getGraphInformation(): String {
        return getString(R.string.elev_cno_information_detail)
    }

    override fun onGnssStatusReceived(gnssStatus: GnssStatus) {
        cnoElevGraphComponent?.plotElevCNoGraph(gnssStatus)
    }

    private fun createTab(title: String): TabLayout.Tab {
        val tab = tabLayout.newTab().setCustomView(R.layout.item_filter_tab)
        tab.customView?.findViewById<TextView>(R.id.tvFilterTitle)?.text = title
        return tab
    }
}
