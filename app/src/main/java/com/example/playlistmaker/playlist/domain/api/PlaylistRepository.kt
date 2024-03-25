package com.example.playlistmaker.playlist.domain.api

import android.net.Uri
import com.example.playlistmaker.playlist.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun insertPlaylist(playlist: Playlist)
    suspend fun addTrackInPlaylist(playlistId: Int, trackId: String)
    suspend fun addDescriptionPlaylist(track: Track)
    suspend fun deletePlaylist(playlistId: Int)
    suspend fun deleteTrackFromPlayList(tracksListInPlaylist: List<Int>?,
                                        playlistId: Int, trackId: Int)
    suspend fun updatePlaylist(playlist: Playlist)
    suspend fun updateDbListOfTracksInAllPlaylists(playlistId: Int, trackId: Int)
    suspend fun getPlaylistById(playlistId: Int): Playlist
    fun getAllPlaylists(): Flow<List<Playlist>>
    fun getTracksOnlyFromPlaylist(tracksIdsList: List<Int>): Flow<List<Track>?>
    fun saveImageFromUri(uri: Uri, picturesDirectoryPath: String): String
}