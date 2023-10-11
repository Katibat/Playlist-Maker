package com.example.playlistmaker.player.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.util.StatePlayer

class PlayerViewModel(val interactor: PlayerInteractor) : ViewModel() {

    private val handler = Handler(Looper.getMainLooper())
    private val runnable = createUpdateTimerTrack()

    private val statePlayerLiveData = MutableLiveData<StatePlayer>()
    fun getStatePlayerLiveData(): LiveData<StatePlayer> = statePlayerLiveData

    private val currentTimerLiveData = MutableLiveData<Long>()
    fun getCurrentTimerLiveData(): LiveData<Long> = currentTimerLiveData

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

    fun play() {
        interactor.startPlayer()
        handler.post(runnable)
        statePlayerLiveData.postValue(StatePlayer.PLAYING)
    }

    fun pause() {
        handler.removeCallbacks(runnable)
        interactor.pausePlayer()
        statePlayerLiveData.postValue(StatePlayer.PAUSED)
    }

    fun destroy() {
        handler.removeCallbacks(runnable)
    }

    fun release() {
        interactor.release()
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
                currentTimerLiveData.postValue(position)
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

        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerViewModel(Creator.providePlayerInteractor())
            }
        }
    }
}