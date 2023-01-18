package com.stellarworker.geonote.data.contracts

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker

interface MapsFragmentViewModelContract {
    fun startLocationUpdates()
    fun stopLocationUpdates()
    fun onZoomInButtonClick(location: LatLng, oldZoom: Float)
    fun onZoomOutButtonClick(location: LatLng, oldZoom: Float)
    fun onDeviceLocationButtonClick()
    fun onMapClick(location: LatLng)
    fun onMarkerClick(marker: Marker)
    fun requestNotGrantedNoAskDialog()
    fun requestRationaleDialog()
    fun restoreMapState()
    fun saveCameraState(location: LatLng, zoomLevel: Float)
}