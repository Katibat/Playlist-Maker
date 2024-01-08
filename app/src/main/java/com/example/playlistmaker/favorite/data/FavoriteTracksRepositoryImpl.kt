package com.example.playlistmaker.favorite.data

import com.example.playlistmaker.db.data.AppDatabase
import com.example.playlistmaker.db.data.TrackDbConvertor
import com.example.playlistmaker.db.data.TrackEntity
import com.example.playlistmaker.favorite.domain.api.FavoriteTracksRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoriteTracksRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConvertor: TrackDbConvertor,
) : FavoriteTracksRepository {

    override suspend fun insertTrack(track: Track) {
        appDatabase.trackDao().insertTrack(trackDbConvertor.mapTrackToTrackEntity(track))
    }

    override suspend fun deleteTrack(track: Track) {
        appDatabase.trackDao().deleteTrack(trackDbConvertor.mapTrackToTrackEntity(track))
    }

    override suspend fun getTracks(): Flow<List<Track>> = flow {
        val tracks = appDatabase.trackDao().getAllTracks()
        emit(convertFromTrackEntity(tracks))
    }

    override suspend fun getIdsTracks(): Flow<List<Int>> = flow {
        val tracksIds = appDatabase.trackDao().getIdsTracks()
        emit(tracksIds)
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConvertor.mapEntityTrackToTrack(track) }
    }
}