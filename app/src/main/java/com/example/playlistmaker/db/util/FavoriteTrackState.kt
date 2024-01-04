package com.example.playlistmaker.db.util

import com.example.playlistmaker.search.domain.models.Track

sealed interface FavoriteTrackState {

    data object Loading : FavoriteTrackState

    data class Content(
        val tracks: List<Track>
    ) : FavoriteTrackState

    data object Empty : FavoriteTrackState
}