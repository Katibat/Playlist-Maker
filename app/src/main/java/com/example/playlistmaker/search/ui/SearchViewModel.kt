package com.example.playlistmaker.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.domain.api.SearchInteractor
import com.example.playlistmaker.search.domain.models.NetworkError
import com.example.playlistmaker.player.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(val interactor: SearchInteractor) : ViewModel() {
    private var historyList = ArrayList<Track>()
    private var searchJob: Job? = null
    private var isClickAllowed = true

    private var searchState = MutableLiveData<StateSearch>()
    fun observeSearchState(): LiveData<StateSearch> = searchState
    private var lastSearchQueryText: String? = null

    init {
        historyList.addAll(interactor.getHistory())
        searchState.postValue(StateSearch.SearchHistory(historyList))
    }

    override fun onCleared() {
        super.onCleared()
        interactor.saveHistory(historyList)
    }

    fun search(query: String) {
        if (query.isEmpty()) return
        renderState(StateSearch.Loading)

        viewModelScope.launch {
            interactor.searchTrack(query).collect { pair ->
                processResult(pair.first, pair.second)
            }
        }
    }

    private fun processResult(searchTracks: List<Track>?, errorMessage: String?) {
        val tracks = mutableListOf<Track>()
        if (searchTracks != null) {
            tracks.addAll(searchTracks)
        }

        when {
            errorMessage != null -> {
                renderState(StateSearch.SearchError(error = NetworkError.NO_CONNECTION))
            }

            tracks.isEmpty() -> {
                renderState(StateSearch.SearchError(error = NetworkError.NOTHING_FOUND))
            }

            else -> {
                renderState(StateSearch.SearchResult(tracks))
            }
        }
    }

    private fun renderState(state: StateSearch) {
        searchState.postValue(state)
    }

    fun addTrackInHistoryList(track: Track) {
        if (historyList.contains(track)) {
            historyList.removeAt(historyList.indexOf(track))
        }
        if (historyList.size >= SIZE_PAGE_HISTORY_LIST) {
            historyList.removeAt(historyList.size - 1)
        }
        historyList.add(0, track)
        interactor.saveHistory(historyList)
    }

    fun clearHistory() {
        historyList.clear()
        searchState.postValue(StateSearch.SearchHistory(historyList))
    }

    fun clearSearchText() {
        searchState.postValue(StateSearch.SearchHistory(historyList))
    }

    fun searchDebounce(changedText: String) {
        if (lastSearchQueryText == changedText) return
        this.lastSearchQueryText = changedText
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY_MILLIS)
            search(changedText)
        }
    }

    fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            searchJob = viewModelScope.launch {
                delay(CLICK_DEBOUNCE_DELAY_MILLIS)
                isClickAllowed = true
            }
        }
        return current
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY_MILLIS = 2000L
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 1000L
        private const val SIZE_PAGE_HISTORY_LIST = 10
    }
}