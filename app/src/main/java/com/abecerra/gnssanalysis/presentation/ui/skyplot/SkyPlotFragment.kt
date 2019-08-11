package com.abecerra.gnssanalysis.presentation.ui.skyplot


import android.app.Activity
import android.graphics.Color
import android.hardware.GeomagneticField
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.location.GnssMeasurementsEvent
import android.location.Location
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.TabLayout
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import com.abecerra.gnssanalysis.R
import com.abecerra.gnssanalysis.core.base.BaseFragment
import com.abecerra.gnssanalysis.core.computation.GnssService
import com.abecerra.gnssanalysis.core.utils.extensions.Data
import com.abecerra.gnssanalysis.core.utils.extensions.DataState
import com.abecerra.gnssanalysis.core.utils.extensions.observe
import com.abecerra.gnssanalysis.core.utils.filterGnssStatus
import com.abecerra.gnssanalysis.core.utils.getCNo
import com.abecerra.gnssanalysis.core.utils.skyplot.MathUtils
import com.abecerra.gnssanalysis.core.utils.takeTwoDecimalsToDouble
import com.abecerra.gnssanalysis.presentation.data.GnssStatus
import com.abecerra.gnssanalysis.presentation.data.SensorData
import com.abecerra.gnssanalysis.presentation.data.StatusData
import kotlinx.android.synthetic.main.fragment_sky_plot.*
import kotlinx.android.synthetic.main.view_cno_indicator.*
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber


class SkyPlotFragment : BaseFragment(), GnssService.GnssServiceOutput.GnssEventsListener {

    private val viewModel: SkyPlotViewModel by viewModel()

    private var selectedConstellation: CONSTELLATION = Companion.CONSTELLATION.ALL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observe(viewModel.status, ::updateStatusData)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sky_plot, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViews()
    }

    override fun onResume() {
        super.onResume()
        tabLayout?.getTabAt(0)?.select()
        skyplot.setHorizonSelected(mPrefs.getSelectedMask().toFloat())
    }

    private fun setViews() {

        tvLegend?.setOnClickListener {
            clLegend?.visibility = if (clLegend.visibility == View.VISIBLE) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }

        tabLayout.setSelectedTabIndicatorColor(Color.TRANSPARENT)

        CONSTELLATION.values().forEach {
            tabLayout.addTab(createTab(it.name))
        }

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
                tab?.position?.let {
                    if (it < CONSTELLATION.values().size) {
                        selectedConstellation = CONSTELLATION.values()[it]
                    }
                }

            }
        })

    }


    //helpers
    private fun createTab(title: String): TabLayout.Tab {
        val tab = tabLayout.newTab().setCustomView(R.layout.item_filter_tab)
        tab.customView?.findViewById<TextView>(R.id.tvFilterTitle)?.text = title
        return tab
    }

    private fun setCNo(status: GnssStatus) {

        val cno = takeTwoDecimalsToDouble(getCNo(status, selectedConstellation))

        if (cno in 10.00..45.00) {
            seekBar?.setProgress(cno.toInt(), true)
        } else {
            tvCnoAvg?.text = "$cno"
        }

        seekBar?.isEnabled = false

        seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, p: Int, fromUser: Boolean) {
                seekBar?.let {
                    tvCnoAvg?.text = "$cno"
                    clIndicator?.x = it.thumb.bounds.exactCenterX()
                }

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

    }

    private fun updateStatusData(data: Data<StatusData>?) {
        data?.let {
            when (it.dataState) {
                DataState.LOADING -> {
                }
                DataState.SUCCESS -> {
                    it.data?.let { statusData ->
                        cn0Content?.text = statusData.CN0
                        numSatsContent?.text = statusData.satellitesCount
                    }
                }
                DataState.ERROR -> {
                }
            }
        }
    }

    override fun onGnssStarted() {
        skyplot.setStarted()
        Timber.d("$FRAGMENT_TAG :::: onGnssStarted")
    }

    override fun onGnssStopped() {
        skyplot.setStopped()
        Timber.d("$FRAGMENT_TAG :::: onGnssStopped")
    }

    override fun onSatelliteStatusChanged(status: android.location.GnssStatus) {
        Timber.d("$FRAGMENT_TAG :::: onSatelliteStatusChanged")
        val filteredGnssStatus = filterGnssStatus(status, selectedConstellation)

        viewModel.obtainStatusParameters(filteredGnssStatus, selectedConstellation)
        skyplot?.setGnssStatus(filteredGnssStatus)
        setCNo(status = filteredGnssStatus)
    }

    override fun onSensorEvent(event: SensorEvent) {
        Timber.d("$FRAGMENT_TAG :::: onSatelliteStatusChanged")
        val sensorData = activity?.getSensorData(event)
        sensorData?.let {
            skyplot.onOrientationChanged(it.orientation, it.tilt)
        }
    }

    override fun onGnssMeasurementsReceived(event: GnssMeasurementsEvent) {
    }

    override fun onNmeaMessageReceived(message: String, timestamp: Long) {
    }

    override fun onLocationReceived(location: Location) {
    }

    companion object {

        private const val FRAGMENT_TAG: String = "SkyPlotFragment"

        enum class CONSTELLATION(var id: Int) {
            ALL(-1), GALILEO(GnssStatus.CONSTELLATION_GALILEO), GPS(GnssStatus.CONSTELLATION_GPS)
        }
    }

    fun Activity.getSensorData(event: SensorEvent): SensorData? {

        val mRotationMatrix = FloatArray(16)
        val mRemappedMatrix = FloatArray(16)
        val mValues = FloatArray(3)
        val mTruncatedRotationVector = FloatArray(4)
        var mTruncateVector = false
        var mFaceTrueNorth: Boolean = true
        var mGeomagneticField: GeomagneticField? = null
        var mSensorManager: SensorManager? = null

        var orientation: Double
        var tilt = java.lang.Double.NaN

        when (event.sensor.type) {
            Sensor.TYPE_ROTATION_VECTOR -> {
                // Modern rotation vector sensors
                if (!mTruncateVector) {
                    try {
                        SensorManager.getRotationMatrixFromVector(mRotationMatrix, event.values)
                    } catch (e: IllegalArgumentException) {
                        // On some Samsung devices, an exception is thrown if this vector > 4 (see #39)
                        // Truncate the array, since we can deal with only the first four values
                        mTruncateVector = true
                        // Do the truncation here the first time the exception occurs
                        System.arraycopy(event.values, 0, mTruncatedRotationVector, 0, 4)
                        SensorManager.getRotationMatrixFromVector(mRotationMatrix, mTruncatedRotationVector)
                    }

                } else {
                    // Truncate the array to avoid the exception on some devices (see #39)
                    System.arraycopy(event.values, 0, mTruncatedRotationVector, 0, 4)
                    SensorManager.getRotationMatrixFromVector(mRotationMatrix, mTruncatedRotationVector)
                }

                val rot = windowManager.defaultDisplay.rotation
                when (rot) {
                    Surface.ROTATION_0 ->
                        // No orientation change, use default coordinate system
                        SensorManager.getOrientation(mRotationMatrix, mValues)
                    Surface.ROTATION_90 -> {
                        // Log.d(TAG, "Rotation-90");
                        SensorManager.remapCoordinateSystem(
                            mRotationMatrix, SensorManager.AXIS_Y,
                            SensorManager.AXIS_MINUS_X, mRemappedMatrix
                        )
                        SensorManager.getOrientation(mRemappedMatrix, mValues)
                    }
                    Surface.ROTATION_180 -> {
                        // Log.d(TAG, "Rotation-180");
                        SensorManager
                            .remapCoordinateSystem(
                                mRotationMatrix, SensorManager.AXIS_MINUS_X,
                                SensorManager.AXIS_MINUS_Y, mRemappedMatrix
                            )
                        SensorManager.getOrientation(mRemappedMatrix, mValues)
                    }
                    Surface.ROTATION_270 -> {
                        // Log.d(TAG, "Rotation-270");
                        SensorManager
                            .remapCoordinateSystem(
                                mRotationMatrix, SensorManager.AXIS_MINUS_Y,
                                SensorManager.AXIS_X, mRemappedMatrix
                            )
                        SensorManager.getOrientation(mRemappedMatrix, mValues)
                    }
                    else ->
                        // This shouldn't happen - assume default orientation
                        SensorManager.getOrientation(mRotationMatrix, mValues)
                }// Log.d(TAG, "Rotation-0");
                // Log.d(TAG, "Rotation-Unknown");
                orientation = Math.toDegrees(mValues[0].toDouble())  // azimuth
                tilt = Math.toDegrees(mValues[1].toDouble())
            }
            Sensor.TYPE_ORIENTATION ->
                // Legacy orientation sensors
                orientation = event.values[0].toDouble()
            else ->
                // A sensor we're not using, so return
                return null
        }

        // Correct for true north, if preference is set
        if (mFaceTrueNorth && mGeomagneticField != null) {
            orientation += mGeomagneticField?.declination?.toDouble() ?: 0.0
            // Make sure value is between 0-360
            orientation = MathUtils.mod(orientation.toFloat(), 360.0f).toDouble()
        }

        return SensorData(orientation, tilt)
    }


}
