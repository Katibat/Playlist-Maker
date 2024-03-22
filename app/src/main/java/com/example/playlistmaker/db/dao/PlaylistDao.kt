package com.example.playlistmaker.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.playlistmaker.db.entity.PlaylistEntity

@Dao
interface PlaylistDao {

    @Insert(entity = PlaylistEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Query("DELETE FROM playlist_table WHERE id = :playlistId")
    suspend fun deletePlaylistById(playlistId: Int)

    @Update(entity = PlaylistEntity::class)
    suspend fun updatePlaylist(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlist_table")
    suspend fun getAllPlaylists(): List<PlaylistEntity>

    @Query("SELECT * FROM playlist_table WHERE id = :playlistId")
    suspend fun getPlaylistById(playlistId: Int): PlaylistEntity

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
    WHERE id = :playlistId""")
    suspend fun addTrackToPlaylist(playlistId: Int, trackId: String)

    @Query("UPDATE playlist_table " +
            "SET tracks_ids = :updatedListOfTracks, count_tracks = count_tracks - 1 " +
            "WHERE id = :playlistId")
    suspend fun deleteTrackFromPlaylist(updatedListOfTracks: String?, playlistId: Int)

    @Query("SELECT * FROM playlist_table WHERE id <> :playlistId")
    suspend fun updatePlaylistTable(playlistId: Int): List<PlaylistEntity>
}