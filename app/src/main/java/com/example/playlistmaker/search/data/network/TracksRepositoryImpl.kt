package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.player.data.dto.TracksRequest
import com.example.playlistmaker.player.data.dto.TracksResponse
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient.Companion.OK_HTTP_CODE
import com.example.playlistmaker.player.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.Resource

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override fun searchTracks(expression: String): Resource<List<Track>> {
        val response = networkClient.doRequest(TracksRequest(expression))
        return when (response.resultCode) {
            -1 -> {
                Resource.Error("Проверьте подключение к интернету")
            }

            OK_HTTP_CODE -> {
                Resource.Success((response as TracksResponse).results.map {
                    Track(
                        it.trackId,
                        it.trackName,
                        it.artistName,
                        it.trackTimeMillis,
                        it.artworkUrl100,
                        it.collectionName,
                        it.releaseDate,
                        it.primaryGenreName,
                        it.country,
                        it.previewUrl
                    )
                })
            }

            else -> {
                Resource.Error("Ошибка сервера")
            }
        }
    }
}