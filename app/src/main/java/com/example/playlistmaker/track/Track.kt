package com.example.playlistmaker.track

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
data class Track (
    val trackId: Int, // уникальный идентификатор трека
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    val trackTimeMillis: Long, // Продолжительность трека
    val artworkUrl100: String, // Ссылка на изображение обложки
    val collectionName: String, // Название альбома
    val releaseDate: String, // Год релиза трека
    val primaryGenreName: String, // Жанр трека
    val country: String // Страна исполнителя
) : Parcelable {

    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")

    fun getTrackTimeMillis(): String? =
        SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis)

    companion object {
        const val TRACK = "track"
    }
}