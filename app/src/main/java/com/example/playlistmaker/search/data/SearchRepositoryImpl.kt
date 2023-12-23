package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.data.network.TracksRequest
import com.example.playlistmaker.search.data.network.TracksResponse
import com.example.playlistmaker.search.domain.api.SearchRepository
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchRepositoryImpl(private val client: NetworkClient,
                           private val storage: SearchHistoryStorage): SearchRepository {

    override fun searchTrack(expression : String) : Flow<Resource<List<Track>>> = flow {
        val response = client.doRequest(TracksRequest(expression))
        when (response.resultCode) {
            -1 -> {
                emit(Resource.Error("Проверьте подключение к интернету"))
            }
            200 -> {
                with(response as TracksResponse) {
                    val data = results.map {
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
                    emit(Resource.Success(data))
                }
            }
            else -> {
                emit(Resource.Error("Ошибка сервера"))
            }
        }
    }
    override fun saveHistory(tracksList: List<Track>) {
        storage.addTrackInHistoryList(tracksList)
    }

    override fun getHistory(): List<Track> {
        return storage.getHistory()
    }

    override fun clearHistory() {
        storage.clearHistoryList()
    }
}