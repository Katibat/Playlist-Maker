package com.example.playlistmaker.player.domain.impl

import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.api.AudioPlayerRepository
import com.example.playlistmaker.utils.State

class AudioPlayerRepositoryImpl : AudioPlayerRepository {
    private var mediaPlayer = MediaPlayer()
    private var playerState = State.STATE_DEFAULT

    override fun prepare(url: String, onChangeState: (s: State) -> Unit) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = State.STATE_PREPARED
            onChangeState(State.STATE_PREPARED)
        }
        mediaPlayer.setOnCompletionListener {
            playerState = State.STATE_PREPARED
            onChangeState(State.STATE_PREPARED)
        }
    }

    override fun start() {
        mediaPlayer.start()
        playerState = State.STATE_PLAYING
    }

    override fun pause() {
        mediaPlayer.pause()
        playerState = State.STATE_PAUSED
    }

    override fun stop() {
        mediaPlayer.stop()
        mediaPlayer.release()
    }

    override fun getPosition() = mediaPlayer.currentPosition.toLong()

    override fun switchedStatePlayer(callback: (State) -> Unit) {
        when (playerState) {
            State.STATE_PLAYING -> {
                pause()
                callback(State.STATE_PAUSED)
            }

            State.STATE_PREPARED, State.STATE_PAUSED, State.STATE_DEFAULT -> {
                start()
                callback(State.STATE_PLAYING)
            }
        }
    }
}