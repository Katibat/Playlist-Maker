package com.example.playlistmaker.player.domain.api

import com.example.playlistmaker.player.domain.util.StatePlayer

interface PlayerInteractor {
    fun preparePlayer(url: String)
    fun startPlayer()
    fun pausePlayer()
    fun resumePlayer()
    fun getPosition() : Long
    fun switchedStatePlayer(callback: (StatePlayer) -> Unit)
}