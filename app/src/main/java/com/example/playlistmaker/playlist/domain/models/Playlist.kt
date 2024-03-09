package com.example.playlistmaker.playlist.domain.models

import java.io.Serializable

data class Playlist(
    val id: Int, // уникальный идентификатор плейлиста
    val name: String, // Название плейлиста
    val description: String?, // Описание плейлиста
    val imageUrl: String?, // Ссылка на изображение плейлиста
    val tracksIds: List<Int>?, // Список идентификаторов треков
    val countTracks: Int? = tracksIds?.size ?: 0 // Количество треков, добавленных в плейлист
) : Serializable