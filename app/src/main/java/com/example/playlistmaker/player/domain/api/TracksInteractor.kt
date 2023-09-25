package com.example.playlistmaker.player.domain.api

import com.example.playlistmaker.player.domain.models.Track

interface TracksInteractor {
    fun searchTracks(expression: String, consumer: MoviesConsumer)

    interface MoviesConsumer {
        fun consume(foundMovies: List<Track>)
    }
}