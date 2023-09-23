package com.example.playlistmaker.player.domain.api

import com.example.playlistmaker.utils.State

interface AudioPlayerRepository {
    fun prepare(url: String, onChangeState: (s: State) -> Unit)
    fun start()
    fun pause()
    fun stop()
    fun getPosition() : Long
    fun switchedStatePlayer(callback: (State) -> Unit)
}