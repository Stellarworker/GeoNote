package com.stellarworker.geonote.utils

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.stellarworker.geonote.data.room.MarkerEntity
import com.stellarworker.geonote.domain.UserMarker

class Mappers {

    fun map(userMarker: UserMarker) =
        MarkerEntity(
            title = userMarker.title,
            description = userMarker.description,
            latitude = userMarker.markerOptions.position.latitude,
            longitude = userMarker.markerOptions.position.longitude
        )

    fun map(markerEntity: MarkerEntity): UserMarker {
        val markerOptions = MarkerOptions()
            .title(markerEntity.title)
            .position(LatLng(markerEntity.latitude, markerEntity.longitude))
        return UserMarker(
            id = markerEntity.id,
            markerOptions = markerOptions,
            title = markerEntity.title,
            description = markerEntity.description
        )
    }

    fun mapEntityList(markerEntityList: List<MarkerEntity>) =
        markerEntityList.map { markerEntity -> map(markerEntity) }
}