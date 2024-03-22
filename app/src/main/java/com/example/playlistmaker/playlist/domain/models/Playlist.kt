package com.example.playlistmaker.playlist.domain.models

import java.io.Serializable

data class Playlist(
    val id: Int, // уникальный идентификатор плейлиста
    var name: String, // Название плейлиста
    var description: String?, // Описание плейлиста
    var imageUrl: String?, // Ссылка на изображение плейлиста
    var tracksIds: List<Int>?, // Список идентификаторов треков
    var countTracks: Int? = tracksIds?.size ?: 0 // Количество треков, добавленных в плейлист
) : Serializable