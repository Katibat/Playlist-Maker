package com.example.playlistmaker.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.playlistmaker.db.entity.PlaylistEntity

@Dao
interface PlaylistDao {

    @Insert(entity = PlaylistEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Delete(entity = PlaylistEntity::class)
    suspend fun deletePlaylist(playlist: PlaylistEntity)

    @Update(entity = PlaylistEntity::class)
    suspend fun updatePlaylist(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlist_table")
    suspend fun getAllPlaylists(): List<PlaylistEntity>

    @Query(
        """
    UPDATE playlist_table 
    SET 
        tracks_ids = CASE 
            WHEN tracks_ids IS NULL OR tracks_ids = '' 
            THEN :trackId 
            ELSE tracks_ids || ',' || :trackId 
            END,
        count_tracks = CASE 
            WHEN count_tracks IS NULL 
            THEN 1 
            ELSE count_tracks + 1 
            END
    WHERE id = :playlistId"""
    )
    suspend fun addTrackToPlaylist(playlistId: Int, trackId: String)
}