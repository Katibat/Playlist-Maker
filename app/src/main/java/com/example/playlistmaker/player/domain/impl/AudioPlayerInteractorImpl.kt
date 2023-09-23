package com.example.playlistmaker.player.domain.impl

import com.example.playlistmaker.player.domain.api.AudioPlayerInteractor
import com.example.playlistmaker.player.domain.api.AudioPlayerRepository
import com.example.playlistmaker.utils.State

class AudioPlayerInteractorImpl(private val repository: AudioPlayerRepository) :
    AudioPlayerInteractor {

    override fun preparePlayer(url: String, onChangeState: (s: State) -> Unit) {
        repository.prepare(url, onChangeState)
    }

    override fun startPlayer() {
        repository.start()
    }

    override fun pausePlayer() {
        repository.pause()
    }

    override fun stopPlayer() {
        repository.stop()
    }

    override fun getPosition(): Long = repository.getPosition()

    override fun switchedStatePlayer(callback: (State) -> Unit) {
        repository.switchedStatePlayer(callback)
    }
}