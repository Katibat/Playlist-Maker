package com.example.playlistmaker.media.ui.playlist

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.media.domain.api.PlaylistInteractor

class PlaylistCreateViewModel(
    private val context: Context,
    private val interactor: PlaylistInteractor
) : ViewModel() {

    private val _imagePathLiveData = MutableLiveData<String>()
    val imagePathLiveData: LiveData<String> get() = _imagePathLiveData

    suspend fun createNewPlaylist(
        name: String,
        description: String,
        imageUrl: String?,
        tracksIds: List<Int>?,
        countTracks: Int?
    ) {
        val newPlaylist = Playlist(0, name, description, imageUrl, tracksIds, countTracks)
        interactor.insertPlaylist(newPlaylist)
    }

    fun saveImage(uri: Uri) {
        val picturesDirectoryPath = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath ?: ""
        val imagePath = interactor.saveImageFromUri(uri, picturesDirectoryPath)
        _imagePathLiveData.postValue(imagePath)
    }

    suspend fun editPlaylist(playlist: Playlist) {
        interactor.insertPlaylist(playlist)
    }
}