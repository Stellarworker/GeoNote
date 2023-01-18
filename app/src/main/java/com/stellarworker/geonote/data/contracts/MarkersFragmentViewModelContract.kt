package com.stellarworker.geonote.data.contracts

import com.stellarworker.geonote.domain.UserMarker

interface MarkersFragmentViewModelContract {
    fun requestMarkers()
    fun removeMarker(id: Long)
    fun updateMarker(userMarker: UserMarker)
    fun requestSnackbar(text: String)
}