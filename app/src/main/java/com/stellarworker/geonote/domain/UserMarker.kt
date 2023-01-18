package com.stellarworker.geonote.domain

import com.google.android.gms.maps.model.MarkerOptions
import com.stellarworker.geonote.utils.EMPTY
import com.stellarworker.geonote.utils.ZERO

data class UserMarker(
    var id: Long = Long.ZERO,
    val markerOptions: MarkerOptions,
    var title: String = String.EMPTY,
    var description: String = String.EMPTY
)