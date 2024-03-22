package com.example.playlistmaker.playlistDetails.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.playlist.domain.api.PlaylistInteractor
import com.example.playlistmaker.playlist.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistDetailsViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {

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
                playlistInteractor.getTracksOnlyFromPlaylist(idsList)
                    .collect { track ->
                        processResult(track)
                    }
            }
        } else {
            _tracksLiveData.postValue(listOf())
        }
    }


//    private fun getTracksFromCurrentPlaylistUpdated(updatedIdsList: List<Int>?) {
//        if (updatedIdsList?.isEmpty() == true || updatedIdsList == null) {
//            processResult(emptyList())
//        } else {
//            viewModelScope.launch {
//                playlistInteractor.getTracksOnlyFromPlaylist(updatedIdsList)
//                    .collect { track ->
//                        processResult(track)
//                    }
//            }
//        }
//    }

    private fun processResult(tracks: List<Track>?) {
        _tracksLiveData.postValue(tracks)
    }

    suspend fun deleteTrackFromPlaylist(track: Track, playlist: Playlist) {
        val idTrackToDelete = track.trackId
        val idsInPlaylist = playlist.tracksIds?.toMutableList()
        idsInPlaylist?.remove(track.trackId)
        playlist.tracksIds = idsInPlaylist?.reversed()
        if (playlist.countTracks != null) {
            playlist.countTracks = playlist.countTracks!! - 1
        }
        _playlistDetails.postValue(playlist)

        playlistInteractor.deleteTrackFromPlayList(
            idsInPlaylist?.toList(),
            playlist.id,
            idTrackToDelete
        )
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

    fun shareTracks(playlist: Playlist, listOfTracks: MutableList<Track>?) {
        val message = generateMessage(playlist, listOfTracks)
        sharingInteractor.shareTrack(message)
    }

    private fun generateMessage(playlist: Playlist, trackList: MutableList<Track>?): String {
        var sharingText =
            with(playlist) {
                "${name}\n" +
                        "${description}\n" +
                        "${countTracks} треков\n"
            }
        if (trackList != null) {
            for (i in trackList.indices) {
                sharingText += "${i + 1}. ${trackList[i].artistName}, " +
                        "${trackList[i].trackName} - (" + "${
                    SimpleDateFormat(
                        "mm:ss",
                        Locale.getDefault()
                    ).format(trackList[i].trackTimeMillis)
                })\n"
            }
        }
        return sharingText
    }
}