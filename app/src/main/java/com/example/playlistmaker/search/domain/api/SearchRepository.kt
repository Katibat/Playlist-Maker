package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.Resource
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun searchTrack(expression : String) : Flow<Resource<List<Track>>>
    fun saveHistory(tracksList: List<Track>)
    fun getHistory(): List<Track>
    fun clearHistory()
}