package com.example.playlistmaker.player.domain.api

import com.example.playlistmaker.player.domain.models.Track

interface TracksRepository {
    fun searchTracks(expression: String): List<Track>
}