package com.example.playlistmaker.player.domain.api

import com.example.playlistmaker.player.domain.util.StatePlayer

interface PlayerInteractor {
    fun preparePlayer(url: String, onChangeState: (s: StatePlayer) -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun getPosition() : Long
    fun switchedStatePlayer(callback: (StatePlayer) -> Unit)
}