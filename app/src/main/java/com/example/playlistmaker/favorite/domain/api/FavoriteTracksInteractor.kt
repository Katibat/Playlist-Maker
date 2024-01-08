package com.example.playlistmaker.favorite.domain.api

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksInteractor {
    suspend fun insertTrack(track: Track)
    suspend fun deleteTrack(track: Track)
    suspend fun getTracks(): Flow<List<Track>>
    suspend fun getIdsTracks(): Flow<List<Int>>
}