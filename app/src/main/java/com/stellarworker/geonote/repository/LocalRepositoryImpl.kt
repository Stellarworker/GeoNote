package com.stellarworker.geonote.repository

import com.stellarworker.geonote.App
import com.stellarworker.geonote.data.room.MarkersDAO
import com.stellarworker.geonote.domain.UserMarker
import com.stellarworker.geonote.utils.Mappers

class LocalRepositoryImpl : LocalRepository {
    private val dataSource: MarkersDAO = App.getMarkersDAO()
    private val mappers = Mappers()
    override fun getAllMarkers(): List<UserMarker> {
        return mappers.mapEntityList(dataSource.getAll())
    }

    override fun saveMarker(userMarker: UserMarker) {
        dataSource.insert(mappers.map(userMarker))
    }

    override fun updateMarker(userMarker: UserMarker) {
        dataSource.updateById(userMarker.id, userMarker.title, userMarker.description)
    }

    override fun removeMarkerByID(id: Long) {
        dataSource.deleteById(id)
    }
}