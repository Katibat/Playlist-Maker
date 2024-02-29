package com.example.playlistmaker.db.convertor

import com.example.playlistmaker.db.entity.PlaylistEntity
import com.example.playlistmaker.media.domain.models.Playlist
import com.google.gson.Gson

class PlaylistDbConvertor() {

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlist.id,
            playlist.name,
            playlist.description,
            playlist.imageUrl,
            convertTracksIdsToString(playlist.tracksIds),
            playlist.tracksIds?.size
        )
    }

    fun map(playlist: PlaylistEntity): Playlist {
        val tracksIds = if (playlist.tracksIds != "null" && !playlist.tracksIds.isNullOrEmpty()) {
            convertStringTracksIdsToList(playlist.tracksIds)
        } else {
            emptyList()
        }
        return Playlist(
            playlist.id,
            playlist.name,
            playlist.description,
            playlist.imageUrl,
            tracksIds,
            playlist.countTracks ?: 0
        )
    }

    private fun convertTracksIdsToString(tracksIds: List<Int>?): String? {
        return if (tracksIds == null) {
            null
        } else {
            Gson().toJson(tracksIds)
        }
    }

    private fun convertStringTracksIdsToList(tracksIds: String?): List<Int> {
        return tracksIds?.split(",")?.mapNotNull { it.toIntOrNull() } ?: emptyList()
    }
}