package com.example.playlistmaker.playlist.ui

import android.content.Context
import android.os.Environment
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.playlist.domain.models.Playlist
import com.example.playlistmaker.playlist.domain.api.PlaylistInteractor
import com.example.playlistmaker.playlist.domain.api.PlaylistImageStorage
import java.io.File

class PlaylistCreateViewModel(
    private val context: Context,
    private val interactor: PlaylistInteractor
) : ViewModel() {

    private val storage: PlaylistImageStorage? = null
    private val imageUrlLiveData = MutableLiveData<String>()
    fun observeImageUrl(): LiveData<String> = imageUrlLiveData

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

    fun getImageUrlFromStorage(playlistName: String) {
        val file = storage?.getImageFileForPlaylist(context, playlistName)
        val url = file?.toUri().toString()
        imageUrlLiveData.postValue(url)
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
}