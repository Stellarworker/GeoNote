package com.stellarworker.geonote.domain.messages

import com.stellarworker.geonote.domain.UserMarker

sealed class MarkerMessage {
    data class UserMarkers(val userMarkers: List<UserMarker>) : MarkerMessage()
    data class InfoSnackBar(val text: String) : MarkerMessage()
    data class InfoToast(val text: String, val length: Int) : MarkerMessage()
}