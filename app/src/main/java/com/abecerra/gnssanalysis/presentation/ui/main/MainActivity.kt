package com.abecerra.gnssanalysis.presentation.ui.main

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.abecerra.gnssanalysis.R
import com.abecerra.gnssanalysis.core.base.BaseGnssActivity
import com.abecerra.gnssanalysis.core.computation.GnssService
import com.abecerra.gnssanalysis.core.utils.CustomFragmentPagerAdapter
import com.abecerra.gnssanalysis.core.utils.view.CustomAHBottomNavigationItem
import com.abecerra.gnssanalysis.presentation.ui.position.PvtComputationFragment
import com.abecerra.gnssanalysis.presentation.ui.skyplot.SkyPlotFragment
import com.abecerra.gnssanalysis.presentation.ui.statistics.StatisticsFragment
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import kotlinx.android.synthetic.main.activity_main.bottomNavigation
import kotlinx.android.synthetic.main.activity_main.viewPager

class MainActivity : BaseGnssActivity(), MainActivityInput {

    private val pvtComputationFragment: PvtComputationFragment = PvtComputationFragment()
    private val skyPlotFragment: SkyPlotFragment = SkyPlotFragment()
    private val statisticsFragment: StatisticsFragment = StatisticsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setViewPager()
        setupBottomNavigation()
    }

    private fun setViewPager() {
        val pagerAdapter = CustomFragmentPagerAdapter<Fragment>(supportFragmentManager)

        pagerAdapter.addFragment(pvtComputationFragment, "Position")
        pagerAdapter.addFragment(skyPlotFragment, "GNSS state")
        pagerAdapter.addFragment(statisticsFragment, "Statistics")

        viewPager.setPagingEnabled(false)
        viewPager.offscreenPageLimit = 5
        viewPager.adapter = pagerAdapter
        viewPager.currentItem = 0
    }

    private fun setupBottomNavigation() {
        val position = CustomAHBottomNavigationItem(
            getString(R.string.position_bottom),
            R.drawable.ic_position
        )
        val status = CustomAHBottomNavigationItem(
            getString(R.string.gnss_state_bottom),
            R.drawable.ic_satellite
        )
        val statistics = CustomAHBottomNavigationItem(
            getString(R.string.statistics_bottom),
            R.drawable.ic_statistics
        )

        val itemList = arrayListOf(position, status, statistics)
        bottomNavigation.addItems(itemList)

        bottomNavigation.titleState = AHBottomNavigation.TitleState.ALWAYS_SHOW

        bottomNavigation.defaultBackgroundColor = ContextCompat.getColor(this, R.color.white)
        bottomNavigation.accentColor = ContextCompat.getColor(this, R.color.colorPrimary)

        bottomNavigation.setOnTabSelectedListener { pos, wasSelected ->
            if (!wasSelected) {
                viewPager.setCurrentItem(pos, false)
            }
            true
        }
    }

    override fun getGnssService(): GnssService? = mService

    override fun onGnssServiceConnected(service: GnssService) {
        mService?.bindGnssEventsListener(skyPlotFragment)
        mService?.bindGnssEventsListener(statisticsFragment)
    }

    override fun onGnssServiceDisconnected() {
        mService?.unbindGnssEventsListener(skyPlotFragment)
        mService?.unbindGnssEventsListener(statisticsFragment)
    }
}
