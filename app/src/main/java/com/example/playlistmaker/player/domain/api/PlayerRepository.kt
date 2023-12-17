package com.example.playlistmaker.player.domain.api

import com.example.playlistmaker.player.domain.util.StatePlayer

interface PlayerRepository {
    fun prepare(url: String)
    fun start()
    fun pause()
    fun reset()
    fun getPosition() : Long
    fun switchedStatePlayer(callback: (StatePlayer) -> Unit)
}