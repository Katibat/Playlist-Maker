package com.example.playlistmaker.media.domain.impl

import android.net.Uri
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.media.domain.api.PlaylistInteractor
import com.example.playlistmaker.media.domain.api.PlaylistRepository
import com.example.playlistmaker.player.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(private val repository: PlaylistRepository) : PlaylistInteractor {

    override suspend fun insertPlaylist(playlist: Playlist) {
        repository.insertPlaylist(playlist)
    }

    override suspend fun deletePlaylist(playlistId: Int) {
        repository.deletePlaylist(playlistId)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        repository.updatePlaylist(playlist)
    }

    override suspend fun updateDbListOfTracksInAllPlaylists(playlistId: Int, trackId: Int) {
        repository.updateDbListOfTracksInAllPlaylists(playlistId, trackId)
    }

    override suspend fun getPlaylistById(playlistId: Int): Playlist {
        return repository.getPlaylistById(playlistId)
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return repository.getAllPlaylists()
    }

    override fun getTracksCurrentPlaylist(tracksIdsList: List<Int>): Flow<List<Track>?> {
        return repository.getTracksOnlyFromPlaylist(tracksIdsList)
    }

    override suspend fun addTrackInPlaylist(playlistId: Int, trackId: String) {
        repository.addTrackInPlaylist(playlistId, trackId)
    }

    override suspend fun addDescriptionPlaylist(track: Track) {
        repository.addDescriptionPlaylist(track)
    }

    override suspend fun deleteTrackFromPlayList(
        tracksListInPlaylist: List<Int>?,
        playlistId: Int,
        trackId: Int
    ) {
        repository.deleteTrackFromPlayList(tracksListInPlaylist, playlistId, trackId)
    }

    override fun saveImageFromUri(uri: Uri, picturesDirectoryPath: String): String {
        return repository.saveImageFromUri(uri, picturesDirectoryPath)
    }
}