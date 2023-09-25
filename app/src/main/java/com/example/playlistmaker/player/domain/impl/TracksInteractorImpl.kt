package com.example.playlistmaker.player.domain.impl

import com.example.playlistmaker.player.domain.api.TracksInteractor
import com.example.playlistmaker.player.domain.api.TracksRepository
import java.util.concurrent.Executors

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(expression: String, consumer: TracksInteractor.MoviesConsumer) {
        executor.execute {
            consumer.consume(repository.searchTracks(expression))
        }
    }
}