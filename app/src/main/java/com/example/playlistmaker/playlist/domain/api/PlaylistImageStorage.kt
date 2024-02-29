package com.example.playlistmaker.playlist.domain.api

import android.content.Context
import android.os.Environment
import java.io.File

object PlaylistImageStorage {

    fun getImageDirectory(context: Context): File {
        return File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playlist")
    }

    fun getTemporaryImageFile(context: Context): File {
        val directory = getImageDirectory(context)
        if (!directory.exists()) {
            directory.mkdirs()
        }
        return File(directory, "image.jpg")
    }

    fun getImageFileForPlaylist(context: Context, playlistName: String): File {
        return File(getImageDirectory(context), "image_$playlistName.jpg")
    }
}