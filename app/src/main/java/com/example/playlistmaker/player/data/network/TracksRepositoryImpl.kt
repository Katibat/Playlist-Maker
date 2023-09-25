package com.example.playlistmaker.player.data.network

import com.example.playlistmaker.player.data.NetworkClient
import com.example.playlistmaker.player.data.dto.TracksRequest
import com.example.playlistmaker.player.data.dto.TracksResponse
import com.example.playlistmaker.player.data.network.RetrofitNetworkClient.Companion.OK_HTTP_CODE
import com.example.playlistmaker.player.domain.api.TracksRepository
import com.example.playlistmaker.player.domain.models.Track

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override fun searchTracks(expression: String): List<Track> {
        val response = networkClient.doRequest(TracksRequest(expression))
        if (response.resultCode == OK_HTTP_CODE) {
            return (response as TracksResponse).results.map {
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
                    it.previewUrl)
            }
        } else {
            return emptyList()
        }
    }
}