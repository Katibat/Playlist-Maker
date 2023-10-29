package com.example.playlistmaker.player.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.util.StatePlayer

class PlayerViewModel(val interactor: PlayerInteractor) : ViewModel() {

    private val handler = Handler(Looper.getMainLooper())
    private val runnable = createUpdateTimerTrack()

    private val statePlayerLiveData = MutableLiveData<StatePlayer>()
    fun getStatePlayerLiveData(): LiveData<StatePlayer> = statePlayerLiveData

    private val currentTimeLiveData = MutableLiveData<Long>()
    fun getCurrentTimeLiveData(): LiveData<Long> = currentTimeLiveData

    init {
        interactor.switchedStatePlayer { state ->
            statePlayerLiveData.postValue(state)
            if (state == StatePlayer.DEFAULT) handler.removeCallbacks(runnable)
        }
    }

    fun prepare(url: String) {
        interactor.preparePlayer(url) { state ->
            when (state) {
                StatePlayer.PREPARED, StatePlayer.DEFAULT -> {
                    statePlayerLiveData.postValue(StatePlayer.PREPARED)
                    handler.removeCallbacks(runnable)
                }
                else -> {
                    handler.removeCallbacks(runnable)
                }
            }
        }
    }

    fun onStart() {
        interactor.startPlayer()
        handler.post(runnable)
        statePlayerLiveData.postValue(StatePlayer.PLAYING)
    }

    fun onPause() {
        handler.removeCallbacks(runnable)
        interactor.pausePlayer()
        statePlayerLiveData.postValue(StatePlayer.PAUSED)
    }

    fun onDestroy() {
        handler.removeCallbacks(runnable)
    }

    fun onResume() {
        handler.removeCallbacks(runnable)
        statePlayerLiveData.postValue(StatePlayer.PAUSED)
    }

    private fun createUpdateTimerTrack(): Runnable {
        return object : Runnable {
            override fun run() {
                val position = interactor.getPosition()
                currentTimeLiveData.postValue(position)
                handler.postDelayed(this, DELAY_MILLIS)
            }
        }
    }

    fun changePlayerState() {
        interactor.switchedStatePlayer { state ->
            when (state) {
                StatePlayer.PLAYING -> {
                    handler.removeCallbacks(runnable)
                    handler.post(runnable)
                    statePlayerLiveData.postValue(StatePlayer.PLAYING)
                }
                StatePlayer.PAUSED -> {
                    handler.removeCallbacks(runnable)
                    statePlayerLiveData.postValue(StatePlayer.PAUSED)
                }
                StatePlayer.PREPARED -> {
                    handler.removeCallbacks(runnable)
                    handler.post(runnable)
                    statePlayerLiveData.postValue(StatePlayer.PREPARED)
                }
                else -> {
                    handler.removeCallbacks(runnable)
                    statePlayerLiveData.postValue(StatePlayer.DEFAULT)
                }
            }
        }
    }

    companion object {
        private const val DELAY_MILLIS = 1000L
    }
}