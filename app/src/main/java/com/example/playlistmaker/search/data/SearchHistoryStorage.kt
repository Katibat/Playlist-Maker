package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.domain.models.Track

interface SearchHistoryStorage {
    fun addTrackInHistoryList(tracksList: List<Track>)
    fun getHistory(): List<Track>
    fun clearHistoryList()
}