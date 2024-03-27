package com.example.playlistmaker.media.util

import com.example.playlistmaker.media.domain.models.Playlist

sealed interface PlaylistResult {
    data class Success(val playlist: Playlist) : PlaylistResult
    data class Canceled(val playlist: Playlist) : PlaylistResult
}