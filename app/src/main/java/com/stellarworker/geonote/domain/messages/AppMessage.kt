package com.stellarworker.geonote.domain.messages

import com.google.android.gms.maps.model.LatLng
import com.stellarworker.geonote.domain.UserMarker

sealed class AppMessage {
    data class UserMarkers(val userMarkers: List<UserMarker>) : AppMessage()
    data class MoveCamera(val animated: Boolean, val location: LatLng, val zoomLevel: Float) :
        AppMessage()

    data class InfoDialog(val title: String, val message: String) : AppMessage()
    data class InfoSnackBar(val text: String) : AppMessage()
    data class InfoToast(val text: String, val length: Int) : AppMessage()
}
