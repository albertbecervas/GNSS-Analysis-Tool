package com.abecerra.gnssanalysis.presentation.ui.map

import android.content.Context
import android.os.Bundle
import com.abecerra.gnssanalysis.core.computation.data.mapper.LatLngMapper
import com.abecerra.gnssanalysis.core.utils.AppSharedPreferences
import com.abecerra.gnssanalysis.core.utils.getModeIcon
import com.abecerra.pvt.computation.data.ComputedPvtData
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.koin.android.ext.android.inject

class MapFragment : SupportMapFragment(), OnMapReadyCallback {

    private val mPrefs: AppSharedPreferences by inject()

    private var mMap: GoogleMap? = null

    private var mListener: MapListener? = null

    private var isFirstMarker = true

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

    fun addMarkerFromPvtResponse(resp: ComputedPvtData) {
        addMarker(LatLngMapper.map(resp.pvtFix.location), resp.computationSettings.name, resp.computationSettings.color)
    }

    private fun addMarker(latLng: LatLng, title: String, color: Int) {
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        markerOptions.title(title)
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(getModeIcon(color)))
        mMap?.addMarker(markerOptions)
    }

    fun updateCamera(resp: ComputedPvtData, isCameraIntercepted: Boolean) {
        if (isFirstMarker) {
            moveCameraWithZoom(LatLngMapper.map(resp.pvtFix.location), isCameraIntercepted)
            isFirstMarker = false
        } else {
            moveCamera(LatLngMapper.map(resp.pvtFix.location), isCameraIntercepted)
        }
    }

    private fun moveCamera(latLng: LatLng, isCameraIntercepted: Boolean) {
        if (!isCameraIntercepted) {
            mMap?.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        }
    }

    private fun moveCameraWithZoom(latLng: LatLng, isCameraIntercepted: Boolean) {
        if (!isCameraIntercepted) {
            mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))
        }
    }

    fun clearMap() {
        mMap?.clear()
    }

    interface MapListener {
        fun onMapGesture()
    }

}