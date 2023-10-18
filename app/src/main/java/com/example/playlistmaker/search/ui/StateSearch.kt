package com.example.playlistmaker.search.ui

import com.example.playlistmaker.search.domain.models.NetworkError
import com.example.playlistmaker.search.domain.models.Track

sealed class StateSearch {
    object Loading : StateSearch()
    class SearchHistory(val tracks: List<Track>): StateSearch()
    class SearchResult(val tracks: List<Track>): StateSearch()
    class SearchError(val error: NetworkError) : StateSearch()
}