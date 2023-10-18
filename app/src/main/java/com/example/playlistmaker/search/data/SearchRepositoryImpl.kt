package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.domain.api.SearchRepository
import com.example.playlistmaker.search.domain.models.NetworkError
import com.example.playlistmaker.search.domain.models.Track

class SearchRepositoryImpl(private val client: NetworkClient,
                           private val storage: SearchHistoryStorage): SearchRepository {

    override fun searchTrack(query: String,
                             onSuccess: (List<Track>) -> Unit,
                             onError: (NetworkError) -> Unit) {
        client.doRequest(query, onSuccess, onError)
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