package com.example.playlistmaker.media.ui.playlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.media.util.PlaylistState
import com.example.playlistmaker.playlist.domain.api.PlaylistInteractor
import kotlinx.coroutines.launch

class MediaPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val statePlaylistLiveData = MutableLiveData<PlaylistState>()
    fun observeStatePlaylist(): LiveData<PlaylistState> = statePlaylistLiveData

    fun fillData() {
        renderState(PlaylistState.Loading)
        viewModelScope.launch {
            playlistInteractor.getAllPlaylists()
                .collect { playlists ->
                    processResult(playlists)
                }
        }
    }

    private fun processResult(playlists: List<Playlist>) {
        if (playlists.isEmpty()) {
            renderState(PlaylistState.Empty)
        } else {
            renderState(PlaylistState.Content(playlists))
        }
    }

    private fun renderState(state: PlaylistState) {
        statePlaylistLiveData.postValue(state)
    }
}