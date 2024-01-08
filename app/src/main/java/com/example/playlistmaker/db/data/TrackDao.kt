package com.example.playlistmaker.db.data

import androidx.annotation.Nullable
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TrackDao {

    @Insert(entity = TrackEntity::class, onConflict = OnConflictStrategy.REPLACE)
    fun insertTrack(track: TrackEntity)

    @Delete(entity = TrackEntity::class)
    fun deleteTrack(track: TrackEntity)

    @Query("SELECT * FROM track_table ORDER BY timestamp DESC")
    fun getAllTracks(): List<TrackEntity>

    @Query("SELECT track_id FROM track_table")
    fun getIdsTracks(): List<Int>
}