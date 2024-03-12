package com.example.playlistmaker.media.util

import com.example.playlistmaker.playlist.domain.models.Playlist

sealed interface PlaylistResult {
    data class Success(val playlist: Playlist) : PlaylistResult
    data class Canceled(val playlist: Playlist) : PlaylistResult
}