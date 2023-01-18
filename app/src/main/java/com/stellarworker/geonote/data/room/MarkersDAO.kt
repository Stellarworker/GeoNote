package com.stellarworker.geonote.data.room

import androidx.room.*

@Dao
interface MarkersDAO {
    @Query("SELECT * FROM MarkerEntity")
    fun getAll(): List<MarkerEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(entity: MarkerEntity)

    @Update
    fun update(entity: MarkerEntity)

    @Delete
    fun delete(entity: MarkerEntity)

    @Query("DELETE FROM MarkerEntity WHERE id = :id")
    fun deleteById(id: Long)

    @Query("UPDATE MarkerEntity SET title = :title, description = :description WHERE id = :id")
    fun updateById(id: Long, title: String, description: String)

    @Query("DELETE FROM MarkerEntity")
    fun clearTable()
}