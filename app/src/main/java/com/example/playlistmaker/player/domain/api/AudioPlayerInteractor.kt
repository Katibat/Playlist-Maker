package com.example.playlistmaker.player.domain.api

import com.example.playlistmaker.utils.State

interface AudioPlayerInteractor {
    fun preparePlayer(url: String, onChangeState: (s: State) -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun stopPlayer()
    fun getPosition() : Long
    fun switchedStatePlayer(callback: (State) -> Unit)
}