package com.example.playlistmaker.player.data

import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.api.PlayerRepository
import com.example.playlistmaker.player.domain.util.StatePlayer

class PlayerRepositoryImpl(private var mediaPlayer: MediaPlayer) : PlayerRepository {
    private var playerStateCallback: ((StatePlayer) -> Unit)? = null

    override fun prepare(url: String) {
        mediaPlayer.setOnPreparedListener {
            playerStateCallback?.invoke(StatePlayer.PREPARED)
        }
        mediaPlayer.setOnCompletionListener {
            playerStateCallback?.invoke(StatePlayer.DEFAULT)
        }
        mediaPlayer.reset()
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
    }

    override fun start() {
        mediaPlayer.start()
        playerStateCallback?.invoke(StatePlayer.PLAYING)
    }

    override fun pause() {
        mediaPlayer.pause()
        playerStateCallback?.invoke(StatePlayer.PAUSED)
    }

    override fun resume() {
        playerStateCallback?.invoke(StatePlayer.PAUSED)
    }

    override fun getPosition() = mediaPlayer.currentPosition.toLong()

    override fun switchedStatePlayer(callback: (StatePlayer) -> Unit) {
        playerStateCallback = callback
    }
}