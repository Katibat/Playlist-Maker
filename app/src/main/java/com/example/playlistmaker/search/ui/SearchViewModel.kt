package com.example.playlistmaker.search.ui

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.search.domain.api.SearchInteractor
import com.example.playlistmaker.search.domain.models.Track

class SearchViewModel(val interactor: SearchInteractor) : ViewModel() {
    private var historyList = ArrayList<Track>()
    private val handler = Handler(Looper.getMainLooper())

    private var searchTrackStatusLiveData = MutableLiveData<StateSearch>()
    fun getSearchTrackStatusLiveData(): LiveData<StateSearch> = searchTrackStatusLiveData

    private var lastSearchQueryText: String? = null

    init {
        historyList.addAll(interactor.getHistory())
        searchTrackStatusLiveData.postValue(StateSearch.SearchHistory(historyList))
    }

    override fun onCleared() {
        super.onCleared()
        interactor.saveHistory(historyList)
    }

    fun search(query: String) {
        if (query.isEmpty()) return
        searchTrackStatusLiveData.postValue(StateSearch.Loading)
        interactor.searchTrack(query,
            onSuccess = { trackList ->
                searchTrackStatusLiveData.postValue(StateSearch.SearchResult(trackList))
            },
            onError = { error ->
                searchTrackStatusLiveData.postValue(StateSearch.SearchError(error))
            }
        )
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

    fun getHistoryList(): List<Track> {
        return interactor.getHistory()
    }

    fun clearHistory() {
        historyList.clear()
        searchTrackStatusLiveData.postValue(StateSearch.SearchHistory(historyList))
    }

    fun clearSearchText() {
        searchTrackStatusLiveData.postValue(StateSearch.SearchHistory(historyList))
    }

    fun searchDebounce(changedText: String) {
        if (lastSearchQueryText == changedText) return
        this.lastSearchQueryText = changedText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST)
        val runnable = Runnable { search(changedText) }
        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY_MILLIS
        handler.postAtTime(runnable, SEARCH_REQUEST, postTime)
    }

    companion object {
        private val SEARCH_REQUEST = Any()
        private const val SEARCH_DEBOUNCE_DELAY_MILLIS = 2000L
        private const val SIZE_PAGE_HISTORY_LIST = 10

        fun getViewModelFactory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchViewModel(Creator.provideSearchInteractor(context))
            }
        }
    }
}