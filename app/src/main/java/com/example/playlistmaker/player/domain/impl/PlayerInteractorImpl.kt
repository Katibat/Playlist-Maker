package com.example.playlistmaker.player.domain.impl

import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.api.PlayerRepository
import com.example.playlistmaker.player.domain.util.StatePlayer

class PlayerInteractorImpl(private val repository: PlayerRepository) :
    PlayerInteractor {

    override fun preparePlayer(url: String) {
        repository.prepare(url)
    }

    override fun startPlayer() {
        repository.start()
    }

    override fun pausePlayer() {
        repository.pause()
    }

    override fun resetPlayer() {
        repository.reset()
    }

    override fun getPosition(): Long = repository.getPosition()

    override fun switchedStatePlayer(callback: (StatePlayer) -> Unit) {
        repository.switchedStatePlayer(callback)
    }
}