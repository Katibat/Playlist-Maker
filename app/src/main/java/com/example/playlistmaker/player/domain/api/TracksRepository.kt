package com.example.playlistmaker.player.domain.api

import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.Resource

interface TracksRepository {
    fun searchTracks(expression: String): Resource<List<Track>>
}