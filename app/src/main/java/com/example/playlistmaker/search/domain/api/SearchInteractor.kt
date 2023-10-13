package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.NetworkError
import com.example.playlistmaker.search.domain.models.Track

interface SearchInteractor {
    fun searchTrack(query: String, onSuccess: (List<Track>) -> Unit, onError: (NetworkError) -> Unit)
    fun saveHistory(tracksList: List<Track>)
    fun getHistory(): List<Track>
    fun clearHistory()
}