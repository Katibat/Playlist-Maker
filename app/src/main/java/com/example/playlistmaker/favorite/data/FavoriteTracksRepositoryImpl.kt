package com.example.playlistmaker.favorite.data

import com.example.playlistmaker.db.AppDatabase
import com.example.playlistmaker.db.convertor.TrackDbConvertor
import com.example.playlistmaker.db.entity.TrackEntity
import com.example.playlistmaker.favorite.domain.api.FavoriteTracksRepository
import com.example.playlistmaker.player.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoriteTracksRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConvertor: TrackDbConvertor,
) : FavoriteTracksRepository {

    override suspend fun insertTrack(track: Track) {
        appDatabase.trackDao().insertTrack(trackDbConvertor.map(track))
    }

    override suspend fun deleteTrack(track: Track) {
        appDatabase.trackDao().deleteTrack(trackDbConvertor.map(track))
    }

    override fun getTracks(): Flow<List<Track>> = flow {
        val tracks = appDatabase.trackDao().getAllTracks()
        emit(convertFromTrackEntity(tracks))
    }

    override fun getIdsTracks(): Flow<List<Int>> = flow {
        val tracksIds = appDatabase.trackDao().getIdsTracks()
        emit(tracksIds)
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConvertor.map(track) }
    }
}