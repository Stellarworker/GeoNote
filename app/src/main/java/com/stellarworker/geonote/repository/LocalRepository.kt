package com.stellarworker.geonote.repository

import com.stellarworker.geonote.domain.UserMarker

interface LocalRepository {
    fun getAllMarkers(): List<UserMarker>
    fun saveMarker(userMarker: UserMarker)
    fun updateMarker(userMarker: UserMarker)
    fun removeMarkerByID(id: Long)
}