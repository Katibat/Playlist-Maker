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

    private val playerState = MutableLiveData<StatePlayer>()
    fun observePlayerState(): LiveData<StatePlayer> = playerState

    private val currentTimeLiveData = MutableLiveData(0L)
    fun observeCurrentTimeLiveData(): LiveData<Long> = currentTimeLiveData

    init {
        interactor.switchedStatePlayer { state ->
            playerState.postValue(state)
            if (state == StatePlayer.DEFAULT) timerJob?.cancel()
        }
    }

    fun prepare(url: String) {
        interactor.preparePlayer(url) { state ->
            when (state) {
                StatePlayer.PREPARED, StatePlayer.DEFAULT -> {
                    playerState.postValue(StatePlayer.PREPARED)
                    timerJob?.cancel()
                    currentTimeLiveData.postValue(DEFAULT_TIMER)
                }
                else -> {}
            }
        }
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(DELAY_MILLIS)
                currentTimeLiveData.postValue(interactor.getPosition())
            }
        }
    }

    fun onStart() {
        interactor.startPlayer()
        playerState.postValue(StatePlayer.PLAYING)
        startTimer()
    }

    fun onPause() {
        interactor.pausePlayer()
        playerState.postValue(StatePlayer.PAUSED)
        timerJob?.cancel()
    }

    fun onResume() {
        playerState.postValue(StatePlayer.PAUSED)
        timerJob?.cancel()
    }

    fun onStop() {
        interactor.stopPlayer()
    }

    fun changePlayerState() {
        interactor.switchedStatePlayer { state ->
            when (state) {
                StatePlayer.PLAYING -> {
                    startTimer()
                    currentTimeLiveData.postValue(interactor.getPosition())
                    playerState.postValue(StatePlayer.PLAYING)
                }
                StatePlayer.PAUSED -> {
                    playerState.postValue(StatePlayer.PAUSED)
                    timerJob?.cancel()
                }
                StatePlayer.PREPARED -> {
                    timerJob?.cancel()
                    startTimer()
                    playerState.postValue(StatePlayer.PREPARED)
                    currentTimeLiveData.postValue(DEFAULT_TIMER)
                }
                else -> {
                    timerJob?.cancel()
                    playerState.postValue(StatePlayer.DEFAULT)
                }
            }
        }
    }

    companion object {
        private const val DELAY_MILLIS = 300L
        private const val DEFAULT_TIMER = 300L
    }
}