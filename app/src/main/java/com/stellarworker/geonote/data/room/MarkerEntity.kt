package com.stellarworker.geonote.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.stellarworker.geonote.utils.EMPTY
import com.stellarworker.geonote.utils.ZERO

@Entity
data class MarkerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = Long.ZERO,
    val title: String = String.EMPTY,
    val description: String = String.EMPTY,
    val latitude: Double = Double.ZERO,
    val longitude: Double = Double.ZERO
)