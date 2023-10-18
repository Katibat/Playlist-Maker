package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.api.SearchInteractor
import com.example.playlistmaker.search.domain.api.SearchRepository
import com.example.playlistmaker.search.domain.models.NetworkError
import com.example.playlistmaker.search.domain.models.Track

class SearchInteractorImpl(private val repository: SearchRepository) : SearchInteractor {

    override fun searchTrack(
        query: String,
        onSuccess: (List<Track>) -> Unit,
        onError: (NetworkError) -> Unit
    ) {
        repository.searchTrack(query, onSuccess, onError)
    }

    override fun saveHistory(tracksList: List<Track>) {
        repository.saveHistory(tracksList)
    }

    override fun getHistory(): List<Track> {
        return repository.getHistory()
    }

    override fun clearHistory() {
        return repository.clearHistory()
    }
}