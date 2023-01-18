package com.stellarworker.geonote.domain

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

data class MapState(
    var lastLocation: LatLng,
    var zoomLevel: Float,
    var locationMarker: MarkerOptions? = null,
)