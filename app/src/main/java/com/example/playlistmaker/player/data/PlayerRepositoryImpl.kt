package com.example.playlistmaker.player.data

import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.api.PlayerRepository
import com.example.playlistmaker.player.domain.util.StatePlayer

class PlayerRepositoryImpl(private var mediaPlayer: MediaPlayer) : PlayerRepository {
    private var playerStatePlayer = StatePlayer.DEFAULT

    override fun prepare(url: String, onChangeState: (s: StatePlayer) -> Unit) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerStatePlayer = StatePlayer.PREPARED
            onChangeState(StatePlayer.PREPARED)
        }
        mediaPlayer.setOnCompletionListener {
            playerStatePlayer = StatePlayer.PREPARED
            onChangeState(StatePlayer.PREPARED)
        }
    }

    override fun start() {
        mediaPlayer.start()
        playerStatePlayer = StatePlayer.PLAYING
    }

    override fun pause() {
        mediaPlayer.pause()
        playerStatePlayer = StatePlayer.PAUSED
    }

    override fun stop() {
        mediaPlayer.stop()
        mediaPlayer.release()
    }

    override fun getPosition() = mediaPlayer.currentPosition.toLong()

    override fun switchedStatePlayer(callback: (StatePlayer) -> Unit) {
        when (playerStatePlayer) {
            StatePlayer.PLAYING -> {
                pause()
                callback(StatePlayer.PAUSED)
            }

            StatePlayer.PREPARED, StatePlayer.PAUSED, StatePlayer.DEFAULT -> {
                start()
                callback(StatePlayer.PLAYING)
            }
        }
    }
}