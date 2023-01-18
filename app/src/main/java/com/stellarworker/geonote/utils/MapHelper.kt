package com.stellarworker.geonote.utils

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.stellarworker.geonote.data.contracts.MapsFragmentViewModelContract
import com.stellarworker.geonote.domain.UserMarker

class MapHelper {

    private var locationMarker: Marker? = null

    private fun setMapUI(map: GoogleMap) {
        with(map.uiSettings) {
            isMyLocationButtonEnabled = false
            isRotateGesturesEnabled = false
            isMapToolbarEnabled = false
            isTiltGesturesEnabled = false
        }
    }

    private fun setMapListeners(map: GoogleMap, viewModel: MapsFragmentViewModelContract) {
        map.setOnMapClickListener { location ->
            viewModel.onMapClick(location)
        }
        map.setOnMarkerClickListener { marker ->
            viewModel.onMarkerClick(marker)
            true
        }
    }

    fun initMap(map: GoogleMap, viewModel: MapsFragmentViewModelContract) {
        setMapUI(map)
        setMapListeners(map, viewModel)
        viewModel.restoreMapState()
    }

    fun addUserMarkers(map: GoogleMap, items: List<UserMarker>) {
        items.forEach { userMarker ->
            map.addMarker(userMarker.markerOptions)
        }
    }

    fun changeLocationMarker(map: GoogleMap, markerOptions: MarkerOptions) {
        locationMarker?.remove()
        locationMarker = map.addMarker(markerOptions)
    }

    fun moveCamera(map: GoogleMap, animated: Boolean, location: LatLng, zoomLevel: Float) {
        val cameraProperties = CameraUpdateFactory.newLatLngZoom(location, zoomLevel)
        if (animated) {
            map.animateCamera(cameraProperties)
        } else {
            map.moveCamera(cameraProperties)
        }
    }
}