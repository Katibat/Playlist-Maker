package com.example.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.util.StatePlayer
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class PlayerViewModel(val interactor: PlayerInteractor) : ViewModel() {

    private var timerJob: Job? = null

    private val statePlayerLiveData = MutableLiveData(StatePlayer.DEFAULT)
    fun observeStatePlayer(): LiveData<StatePlayer> = statePlayerLiveData

    private val currentTimeLiveData = MutableLiveData<Long>()
    fun observeCurrentTime(): LiveData<Long> = currentTimeLiveData

    fun prepare(url: String) {
        interactor.preparePlayer(url) { state ->
            when (state) {
                StatePlayer.PREPARED -> {
                    statePlayerLiveData.postValue(StatePlayer.PREPARED)
                    timerJob?.cancel()
                    currentTimeLiveData.postValue(DEFAULT_TIMER)
                }

                StatePlayer.DEFAULT -> {
                    statePlayerLiveData.postValue(StatePlayer.DEFAULT)
                    timerJob?.cancel()
                    currentTimeLiveData.postValue(DEFAULT_TIMER)
                }

                else -> Unit
            }
        }
    }

    fun onStart() {
        if (statePlayerLiveData.value == StatePlayer.PREPARED ||
            statePlayerLiveData.value == StatePlayer.DEFAULT) {
            currentTimeLiveData.postValue(DEFAULT_TIMER)
        } else {
            currentTimeLiveData.postValue(interactor.getPosition())
            statePlayerLiveData.postValue(StatePlayer.PLAYING)
            startTimer()
        }
    }

    fun onPause() {
        if (statePlayerLiveData.value == StatePlayer.PLAYING) {
            timerJob?.cancel()
            interactor.pausePlayer()
            statePlayerLiveData.postValue(StatePlayer.PAUSED)
        }
        timerJob?.cancel()
        currentTimeLiveData.postValue(interactor.getPosition())
        statePlayerLiveData.postValue(StatePlayer.PAUSED)
    }

    fun onResume() {
        if (statePlayerLiveData.value == StatePlayer.PLAYING ||
            statePlayerLiveData.value == StatePlayer.PAUSED) {
            timerJob?.cancel()
            interactor.pausePlayer()
            statePlayerLiveData.postValue(StatePlayer.PAUSED)
        }
            statePlayerLiveData.postValue(StatePlayer.PAUSED)
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(DELAY_MILLIS)
                currentTimeLiveData.postValue(interactor.getPosition())
            }
        }
    }

    fun changePlayerState() {
        interactor.switchedStatePlayer { state ->
            when (state) {
                StatePlayer.PLAYING -> {
                    startTimer()
                    currentTimeLiveData.postValue(interactor.getPosition())
                    statePlayerLiveData.postValue(StatePlayer.PLAYING)
                }

                StatePlayer.PAUSED -> {
                    statePlayerLiveData.postValue(StatePlayer.PAUSED)
                    timerJob?.cancel()
                }

                StatePlayer.PREPARED -> {
                    timerJob?.cancel()
                    statePlayerLiveData.postValue(StatePlayer.PREPARED)
                    currentTimeLiveData.postValue(DEFAULT_TIMER)
                }

                StatePlayer.DEFAULT -> {
                    timerJob?.cancel()
                    statePlayerLiveData.postValue(StatePlayer.DEFAULT)
                    currentTimeLiveData.postValue(DEFAULT_TIMER)
                }
            }
        }
    }

    companion object {
        private const val DELAY_MILLIS = 300L
        private const val DEFAULT_TIMER = 0L
    }
}