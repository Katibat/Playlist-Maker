package com.example.playlistmaker.media.util

import com.example.playlistmaker.media.domain.models.Playlist

sealed interface PlaylistState {
    object Loading : PlaylistState
    object Empty : PlaylistState
    data class Content(val playlist: List<Playlist>) : PlaylistState
}