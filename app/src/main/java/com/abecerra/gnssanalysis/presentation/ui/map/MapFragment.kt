package com.abecerra.gnssanalysis.presentation.ui.map

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import com.abecerra.gnssanalysis.R
import com.abecerra.gnssanalysis.app.utils.AppSharedPreferences
import com.abecerra.gnssanalysis.app.utils.getModeIcon
import com.abecerra.pvt_computation.data.output.PvtOutputData
import com.abecerra.pvt_acquisition.data.mapper.LatLngMapper
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.dialog_map_terrain.view.*
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

    override fun onAttach(context: Context) {
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

    fun addMarkerFromPvtResponse(resp: PvtOutputData) {
        addMarker(
            LatLngMapper.map(resp.pvtFix.pvtLatLng),
            resp.computationSettings.name,
            resp.computationSettings.color
        )
    }

    private fun addMarker(latLng: LatLng, title: String, color: Int) {
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        markerOptions.title(title)
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(getModeIcon(color)))
        mMap?.addMarker(markerOptions)
    }

    fun updateCamera(resp: PvtOutputData, isCameraIntercepted: Boolean) {
        if (isFirstMarker) {
            moveCameraWithZoom(LatLngMapper.map(resp.pvtFix.pvtLatLng), isCameraIntercepted)
            isFirstMarker = false
        } else {
            moveCamera(LatLngMapper.map(resp.pvtFix.pvtLatLng), isCameraIntercepted)
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

    fun showMapTypeDialog() {

        val dialog = AlertDialog.Builder(context).create()
        val layout = View.inflate(context, R.layout.dialog_map_terrain, null)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setView(layout)

        layout?.let { view ->

            mMap?.let { gMap ->
                when (gMap.mapType) {
                    GoogleMap.MAP_TYPE_NORMAL -> {
                        view.ivNormalTick.visibility = View.VISIBLE
                        view.ivTerrainTick.visibility = View.GONE
                        view.ivHybridTick.visibility = View.GONE
                        view.ivSatelliteTick.visibility = View.GONE
                    }
                    GoogleMap.MAP_TYPE_TERRAIN -> {
                        view.ivTerrainTick.visibility = View.VISIBLE
                        view.ivNormalTick.visibility = View.GONE
                        view.ivHybridTick.visibility = View.GONE
                        view.ivSatelliteTick.visibility = View.GONE
                    }
                    GoogleMap.MAP_TYPE_HYBRID -> {
                        view.ivHybridTick.visibility = View.VISIBLE
                        view.ivNormalTick.visibility = View.GONE
                        view.ivTerrainTick.visibility = View.GONE
                        view.ivSatelliteTick.visibility = View.GONE
                    }
                    GoogleMap.MAP_TYPE_SATELLITE -> {
                        view.ivSatelliteTick.visibility = View.VISIBLE
                        view.ivHybridTick.visibility = View.GONE
                        view.ivNormalTick.visibility = View.GONE
                        view.ivTerrainTick.visibility = View.GONE
                    }
                }
            }

            view.clNormal.setOnClickListener {
                view.ivNormalTick.visibility = View.VISIBLE
                view.ivTerrainTick.visibility = View.GONE
                view.ivHybridTick.visibility = View.GONE
                view.ivSatelliteTick.visibility = View.GONE

                mMap?.mapType = GoogleMap.MAP_TYPE_NORMAL
                dialog.dismiss()
            }
            view.clTerrain.setOnClickListener {
                view.ivTerrainTick.visibility = View.VISIBLE
                view.ivNormalTick.visibility = View.GONE
                view.ivHybridTick.visibility = View.GONE
                view.ivSatelliteTick.visibility = View.GONE

                mMap?.mapType = GoogleMap.MAP_TYPE_TERRAIN
                dialog.dismiss()
            }
            view.clHybrid.setOnClickListener {
                view.ivHybridTick.visibility = View.VISIBLE
                view.ivNormalTick.visibility = View.GONE
                view.ivTerrainTick.visibility = View.GONE
                view.ivSatelliteTick.visibility = View.GONE

                mMap?.mapType = GoogleMap.MAP_TYPE_HYBRID
                dialog.dismiss()
            }
            view.clSatellite.setOnClickListener {
                view.ivSatelliteTick.visibility = View.VISIBLE
                view.ivHybridTick.visibility = View.GONE
                view.ivNormalTick.visibility = View.GONE
                view.ivTerrainTick.visibility = View.GONE

                mMap?.mapType = GoogleMap.MAP_TYPE_SATELLITE
                dialog.dismiss()
            }

        }
        dialog.show()


    }


    interface MapListener {
        fun onMapGesture()
    }
}
