package com.example.playlistmaker.media.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.api.PlaylistInteractor
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.player.domain.models.Track
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlaylistDetailsViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {

    private var isClickAllowed = true
    private val _playlistDetails = MutableLiveData<Playlist>()
    val playlistDetails: LiveData<Playlist> get() = _playlistDetails

    private val _tracksLiveData = MutableLiveData<List<Track>?>()
    val tracksLiveData: LiveData<List<Track>?> get() = _tracksLiveData

    fun getPlaylistById(playlistId: Int) {
        viewModelScope.launch {
            val playlist = playlistInteractor.getPlaylistById(playlistId)
            _playlistDetails.value = playlist
        }
    }

    fun getTracksFromCurrentPlaylist(playlist: Playlist) {
        val idsList = playlist.tracksIds?.toList()
        if (idsList != null) {
            viewModelScope.launch {
                playlistInteractor.getTracksCurrentPlaylist(idsList)
                    .collect { track ->
                        _tracksLiveData.postValue(track)
                    }
            }
        } else {
            _tracksLiveData.postValue(listOf())
        }
    }

    suspend fun deleteTrackFromPlaylist(track: Track, playlist: Playlist) {
        val idTrackToDelete = track.trackId
        val idsInPlaylist = playlist.tracksIds?.toMutableList()
        idsInPlaylist?.remove(track.trackId)
        playlist.tracksIds = idsInPlaylist?.reversed()
        if (playlist.countTracks != 0) {
            playlist.countTracks = playlist.countTracks!!.toInt() - 1
        }
        _playlistDetails.postValue(playlist)

        playlistInteractor.deleteTrackFromPlayList(
            idsInPlaylist?.toList(),
            playlist.id,
            idTrackToDelete)
        getTracksFromCurrentPlaylist(playlist)
        viewModelScope.launch(Dispatchers.IO) {
            updateDbTracksInAllPlaylists(playlist.id, idTrackToDelete)
        }
    }

    private suspend fun updateDbTracksInAllPlaylists(playlistId: Int, idTrackToDelete: Int) {
        playlistInteractor.updateDbListOfTracksInAllPlaylists(playlistId, idTrackToDelete)
    }

    suspend fun deletePlaylistById(playlistId: Int, trackList: List<Track>) {
        playlistInteractor.deletePlaylist(playlistId)
        viewModelScope.launch(Dispatchers.IO) {
            for (track in trackList) {
                val idTrackToDelete = track.trackId
                updateDbTracksInAllPlaylists(playlistId, idTrackToDelete)
            }
        }
    }

    fun shareTracks(playlist: Playlist, tracks: List<Track>) {
        sharingInteractor.shareTrack(generateMessage(playlist, tracks))
    }

    private fun generateMessage(playlist: Playlist, tracks: List<Track>): String {
        val sb = StringBuilder()
        sb.append(playlist.name).append("\n")
        if (playlist.description?.isNotEmpty() == true) {
            sb.append(playlist.description).append("\n")
        }
        val trackWord = getTrackWordForm(tracks.size)
        sb.append(tracks.size).append(trackWord).append("\n")
        tracks.forEachIndexed { index, track ->
            val trackDuration = convertMillisToTimeFormat(track.trackTimeMillis)
            sb.append("${index + 1}. ${track.artistName} - ${track.trackName} ($trackDuration)")
                .append("\n")
        }
        return sb.toString()
    }

    private fun convertMillisToTimeFormat(millis: Long): String {
        val minutes = millis / 1000 / 60
        val seconds = millis / 1000 % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun getTrackWordForm(count: Int): String {
        return when {
            count % 100 in 11..14 -> "треков"
            count % 10 == 1 -> "трек"
            count % 10 in 2..4 -> "трека"
            else -> "треков"
        }
    }

    fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            viewModelScope.launch {
                delay(CLICK_DEBOUNCE_DELAY_MILLIS)
                isClickAllowed = true
            }
        }
        return current
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 1000L
    }
}