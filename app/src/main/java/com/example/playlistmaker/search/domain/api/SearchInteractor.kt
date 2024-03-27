package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.player.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface SearchInteractor {
    fun searchTrack(expression : String) : Flow<Pair<List<Track>?, String?>>
    fun saveHistory(tracksList: List<Track>)
    fun getHistory(): List<Track>
    fun clearHistory()
}