package com.example.playlistmaker.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.db.entity.TrackInPlaylistEntity

@Dao
interface TrackInPlaylistDao {

    @Insert(entity = TrackInPlaylistEntity::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrackInPlaylist(track: TrackInPlaylistEntity)

    @Query("SELECT * FROM track_table WHERE track_id = :trackId")
    suspend fun getTrackById(trackId: Int): TrackInPlaylistEntity?
}