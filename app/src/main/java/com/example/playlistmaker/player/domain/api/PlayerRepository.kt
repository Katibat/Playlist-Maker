package com.example.playlistmaker.player.domain.api

import com.example.playlistmaker.player.domain.util.StatePlayer

interface PlayerRepository {
    fun prepare(url: String, onChangeState: (s: StatePlayer) -> Unit)
    fun start()
    fun pause()
    fun getPosition() : Long
    fun switchedStatePlayer(callback: (StatePlayer) -> Unit)
}