package com.abecerra.gnssanalysis.presentation.ui.map

import android.content.Context
import android.os.Bundle
import com.abecerra.gnssanalysis.core.utils.AppSharedPreferences
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import org.koin.android.ext.android.inject

class MapFragment : SupportMapFragment(), OnMapReadyCallback {

    private val mPrefs: AppSharedPreferences by inject()

    private var mMap: GoogleMap? = null

    private var mListener: MapListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getMapAsync(this)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mListener = context as? MapListener
    }

    override fun onMapReady(map: GoogleMap?) {
        mMap = map

        mMap?.let {
            with(it) {
                uiSettings?.isMyLocationButtonEnabled = false
                uiSettings?.isMapToolbarEnabled = false
                mapType = mPrefs.getSelectedMapType()

                setOnCameraMoveStartedListener { reason ->
                    when (reason) {
                        GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE -> {
                            mListener?.onMapGesture()
                        }
                    }
                }
            }
        }

    }

    fun clearMap() {
        mMap?.clear()
    }

    interface MapListener {
        fun onMapGesture()
    }

}