package com.example.playlistmaker.media.domain.api

import android.net.Uri
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.player.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
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
    fun getTracksCurrentPlaylist(tracksIdsList: List<Int>): Flow<List<Track>?>
    fun saveImageFromUri(uri: Uri, picturesDirectoryPath: String): String
}