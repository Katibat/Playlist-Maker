package com.example.playlistmaker.media.data

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.example.playlistmaker.db.AppDatabase
import com.example.playlistmaker.db.convertor.PlaylistDbConvertor
import com.example.playlistmaker.db.convertor.TrackInPlaylistDbConvertor
import com.example.playlistmaker.db.entity.PlaylistEntity
import com.example.playlistmaker.db.entity.TrackInPlaylistEntity
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.media.domain.api.PlaylistRepository
import com.example.playlistmaker.player.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val convertorPlaylist: PlaylistDbConvertor,
    private val convertorTracksInPlaylist: TrackInPlaylistDbConvertor,
    private val contentResolver: ContentResolver
) : PlaylistRepository {

    override suspend fun insertPlaylist(playlist: Playlist) {
        val playlistEntity = convertFromPlaylistToEntity(playlist)
        appDatabase.playlistDao().insertPlaylist(playlistEntity)
    }

    override suspend fun deletePlaylist(playlistId: Int) {
        appDatabase.playlistDao().deletePlaylistById(playlistId)
    }

    override suspend fun deleteTrackFromPlayList(
        tracksListInPlaylist: List<Int>?,
        playlistId: Int, trackId: Int
    ) {
        val updatedPlaylist = tracksListInPlaylist?.joinToString(separator = ",")
        appDatabase.playlistDao().deleteTrackFromPlaylist(updatedPlaylist, playlistId)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        val playlistEntity = convertFromPlaylistToEntity(playlist)
        appDatabase.playlistDao().updatePlaylist(playlistEntity)
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> = flow { // !!!
        val playlists = appDatabase.playlistDao().getAllPlaylists()
        emit(convertFromPlaylistEntity(playlists))
    }

    override suspend fun getPlaylistById(playlistId: Int): Playlist {
        val playlist = appDatabase.playlistDao().getPlaylistById(playlistId)
        return convertFromPlaylistEntityOnePlaylist(playlist)
    }

    override suspend fun updateDbListOfTracksInAllPlaylists(playlistId: Int, trackId: Int) {
        val playlists = appDatabase.playlistDao().updatePlaylistTable(playlistId)
        val hasTrack = playlists.any { playlist ->
            val trackIdsAsString = playlist.tracksIds?.split(",")
            val trackIdsAsInt = trackIdsAsString?.mapNotNull {
                val trimmed = it.trim()
                if (trimmed.isNotEmpty()) {
                    trimmed.toInt()
                } else {
                    null
                }
            }
            trackIdsAsInt?.contains(trackId) ?: false
        }
        if (!hasTrack) {
            appDatabase.trackInPlaylistDao().deleteTrackByIdFromTracksListInPlaylist(trackId)
        }
    }

    override suspend fun addTrackInPlaylist(playlistId: Int, trackId: String) {
        appDatabase.playlistDao().addTrackToPlaylist(playlistId, trackId)
    }

    override suspend fun addDescriptionPlaylist(track: Track) {
        val trackEntity = convertFromTrackToTrackEntity(track)
        appDatabase.trackInPlaylistDao().insertTrackInPlaylist(trackEntity)
    }

    override fun getTracksOnlyFromPlaylist(tracksIdsList: List<Int>): Flow<List<Track>?> = flow {
        val tracksInAllPlaylists = appDatabase.trackInPlaylistDao().getAllTracksFromPlaylist()

        if (tracksInAllPlaylists == null) {
            emit(null)
        } else {
            val filteredTracks = tracksInAllPlaylists.filter { track ->
                tracksIdsList.contains(track.trackId)
            }
            val sortedTracks = filteredTracks.sortedBy { track ->
                tracksIdsList.indexOf(track.trackId)
            }.reversed()
            if (sortedTracks.isEmpty()) {
                emit(null)
            } else {
                emit(convertFromTrackInPlaylistEntityToTrack(sortedTracks))
            }
        }
    }

    private fun convertFromPlaylistEntityOnePlaylist(playlist: PlaylistEntity): Playlist {
        return convertorPlaylist.map(playlist)
    }

    private fun convertFromTrackInPlaylistEntityToTrack(tracks: List<TrackInPlaylistEntity>): List<Track>? {
        return tracks.map { track -> convertorTracksInPlaylist.map(track) }
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

    override fun saveImageFromUri(uri: Uri, picturesDirectoryPath: String): String {
        val uniqueID = UUID.randomUUID().toString()
        val file = File(picturesDirectoryPath, "image_$uniqueID.jpg")

        val inputStream = contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)

        BitmapFactory.decodeStream(inputStream).compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
        inputStream?.close()
        outputStream.close()

        return file.absolutePath
    }
}