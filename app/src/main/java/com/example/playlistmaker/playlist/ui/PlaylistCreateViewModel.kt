package com.example.playlistmaker.playlist.ui

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.playlist.domain.models.Playlist
import com.example.playlistmaker.playlist.domain.api.PlaylistInteractor
import java.io.File

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

    fun renameImageFile(playlistName: String) {
        val filePath = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playlist")
        val temporaryFileName = "image.jpg"
        val temporaryFile = File(filePath, temporaryFileName)

        if (temporaryFile.exists()) {
            val finalFile = File(filePath, "image_$playlistName.jpg")
            temporaryFile.renameTo(finalFile)
        }
    }

    suspend fun editPlaylist(playlist: Playlist) {
        interactor.insertPlaylist(playlist)
    }
}