package com.example.playlistmaker.media.util

import com.example.playlistmaker.player.domain.models.Track

sealed interface FavoriteTrackState {

    data object Loading : FavoriteTrackState

    data class Content(
        val tracks: List<Track>
    ) : FavoriteTrackState

    data object Empty : FavoriteTrackState
}