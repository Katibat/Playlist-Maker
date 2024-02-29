package com.example.playlistmaker.playlist.data

import com.example.playlistmaker.db.AppDatabase
import com.example.playlistmaker.db.convertor.PlaylistDbConvertor
import com.example.playlistmaker.db.convertor.TrackInPlaylistDbConvertor
import com.example.playlistmaker.db.entity.PlaylistEntity
import com.example.playlistmaker.db.entity.TrackInPlaylistEntity
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.playlist.domain.api.PlaylistRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val convertorPlaylist: PlaylistDbConvertor,
    private val convertorTracksInPlaylist: TrackInPlaylistDbConvertor
) : PlaylistRepository {

    override suspend fun insertPlaylist(playlist: Playlist) {
        val playlistEntity = convertFromPlaylistToEntity(playlist)
        appDatabase.playlistDao().insertPlaylist(playlistEntity)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        val playlistEntity = convertFromPlaylistToEntity(playlist)
        appDatabase.playlistDao().deletePlaylist(playlistEntity)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        val playlistEntity = convertFromPlaylistToEntity(playlist)
        appDatabase.playlistDao().updatePlaylist(playlistEntity)
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> = flow { // !!!
        val playlists = appDatabase.playlistDao().getAllPlaylists()
        emit(convertFromPlaylistEntity(playlists))
    }

    override suspend fun addTrackInPlaylist(playlistId: Int, trackId: String) {
        appDatabase.playlistDao().addTrackToPlaylist(playlistId, trackId)
    }

    override suspend fun addDescriptionPlaylist(track: Track) {
        val trackEntity = convertFromTrackToTrackEntity(track)
        appDatabase.trackInPlaylistDao().insertTrackInPlaylist(trackEntity)
    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> convertorPlaylist.map(playlist) }
    }

    private fun convertFromPlaylistToEntity(playlist: Playlist): PlaylistEntity {
        return convertorPlaylist.map(playlist)
    }

    private fun convertFromTrackToTrackEntity(track: Track): TrackInPlaylistEntity {
        return convertorTracksInPlaylist.map(track)
    }
}