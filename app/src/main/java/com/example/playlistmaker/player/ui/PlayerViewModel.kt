package com.example.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.favorite.domain.api.FavoriteTracksInteractor
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.media.util.PlaylistResult
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.util.StatePlayer
import com.example.playlistmaker.playlist.domain.api.PlaylistInteractor
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class PlayerViewModel(
    val playerInteractor: PlayerInteractor,
    val trackInteractor: FavoriteTracksInteractor,
    val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private var timerJob: Job? = null
    private var isFavorite = false

    private val statePlayerLiveData = MutableLiveData(StatePlayer.DEFAULT)
    fun observeStatePlayer(): LiveData<StatePlayer> = statePlayerLiveData

    private val currentTimeLiveData = MutableLiveData<Long>()
    fun observeCurrentTime(): LiveData<Long> = currentTimeLiveData

    private val favoriteTracksLiveData = MutableLiveData<Boolean>()
    fun observeFavoriteTracks(): LiveData<Boolean> = favoriteTracksLiveData

    private var playlistsLivaData = MutableLiveData<List<Playlist>>()
    fun observePlaylistLiveData(): LiveData<List<Playlist>> = playlistsLivaData

    private var trackInPlaylistLiveData = MutableLiveData<PlaylistResult>()
    fun observeTrackInPlaylistLiveData(): LiveData<PlaylistResult> = trackInPlaylistLiveData

    private var listOfPlaylistsLiveData = MutableLiveData<List<Playlist>>()
    fun observeListOfPlaylistsLiveData(): LiveData<List<Playlist>> = listOfPlaylistsLiveData

    fun prepare(url: String) {
        playerInteractor.preparePlayer(url) { state ->
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
            statePlayerLiveData.value == StatePlayer.DEFAULT
        ) {
            currentTimeLiveData.postValue(DEFAULT_TIMER)
        } else {
            currentTimeLiveData.postValue(playerInteractor.getPosition())
            statePlayerLiveData.postValue(StatePlayer.PLAYING)
            startTimer()
        }
    }

    fun onPause() {
        if (statePlayerLiveData.value == StatePlayer.PLAYING) {
            timerJob?.cancel()
            playerInteractor.pausePlayer()
            statePlayerLiveData.postValue(StatePlayer.PAUSED)
        }
    }

    fun onResume() {
        getListOfPlaylist()
        if (statePlayerLiveData.value == StatePlayer.PLAYING ||
            statePlayerLiveData.value == StatePlayer.PAUSED
        ) {
            timerJob?.cancel()
            playerInteractor.pausePlayer()
            statePlayerLiveData.postValue(StatePlayer.PAUSED)
        }
    }

    private fun startTimer() {
        if (statePlayerLiveData.value == StatePlayer.DEFAULT ||
            statePlayerLiveData.value == StatePlayer.PREPARED
        ) {
            currentTimeLiveData.postValue(DEFAULT_TIMER)
        }
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(DELAY_MILLIS)
                currentTimeLiveData.postValue(playerInteractor.getPosition())
            }
        }
    }

    fun changePlayerState() {
        playerInteractor.switchedStatePlayer { state ->
            when (state) {
                StatePlayer.PLAYING -> {
                    startTimer()
                    currentTimeLiveData.postValue(playerInteractor.getPosition())
                    statePlayerLiveData.postValue(StatePlayer.PLAYING)
                }

                StatePlayer.PAUSED -> {
                    statePlayerLiveData.postValue(StatePlayer.PAUSED)
                    timerJob?.cancel()
                }

                StatePlayer.PREPARED -> {
                    timerJob?.cancel()
                    currentTimeLiveData.postValue(DEFAULT_TIMER)
                    statePlayerLiveData.postValue(StatePlayer.PREPARED)
                }

                StatePlayer.DEFAULT -> {
                    timerJob?.cancel()
                    currentTimeLiveData.postValue(DEFAULT_TIMER)
                    statePlayerLiveData.postValue(StatePlayer.DEFAULT)

                }
            }
        }
    }

    fun checkIsFavorite(trackId: Int): Boolean {
        val idsList: MutableList<Int> = mutableListOf()
        viewModelScope.launch {
            trackInteractor
                .getIdsTracks()
                .collect { list ->
                    idsList.addAll(list)
                }
        }
        return if (idsList.contains(trackId)) {
            favoriteTracksLiveData.postValue(true)
            isFavorite = true
            true
        } else {
            favoriteTracksLiveData.postValue(false)
            isFavorite = false
            false
        }
    }

    fun onFavoriteClicked(track: Track) {
        viewModelScope.launch {
            if (track.isFavorite) {
                trackInteractor.deleteTrack(track)
                favoriteTracksLiveData.postValue(false)
                track.isFavorite = false
                isFavorite = false
            } else {
                trackInteractor.insertTrack(track)
                favoriteTracksLiveData.postValue(true)
                track.isFavorite = true
                isFavorite = true
            }
        }
    }

    suspend fun addTrackToPlaylist(playlist: Playlist, track: Track) {
        if (playlist.tracksIds?.contains(track.trackId) == true) {
            trackInPlaylistLiveData.postValue(PlaylistResult.Canceled(playlist))
        } else {
            viewModelScope.launch {
                playlistInteractor.addTrackInPlaylist(playlist.id, track.trackId.toString())
                playlistInteractor.addDescriptionPlaylist(track)
                trackInPlaylistLiveData.postValue(PlaylistResult.Success(playlist))
                val updatePlaylists = playlistInteractor.getAllPlaylists().last()
                playlistsLivaData.postValue(updatePlaylists)
            }
        }
    }

    fun getListOfPlaylist() {
        viewModelScope.launch {
            val playlists = playlistInteractor.getAllPlaylists().last()
            listOfPlaylistsLiveData.postValue(playlists)
        }
    }

    companion object {
        private const val DELAY_MILLIS = 300L
        private const val DEFAULT_TIMER = 0L
    }
}