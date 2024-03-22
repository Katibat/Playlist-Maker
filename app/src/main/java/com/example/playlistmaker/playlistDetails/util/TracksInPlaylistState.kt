package com.example.playlistmaker.playlistDetails.util

import com.example.playlistmaker.search.domain.models.Track

sealed interface TracksInPlaylistState {
    object Loading : TracksInPlaylistState
    object Empty : TracksInPlaylistState
    data class Content(val tracks: List<Track>, val trackDurations: Int) : TracksInPlaylistState
}