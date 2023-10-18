package com.example.playlistmaker.search.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.domain.models.NetworkError
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.models.Track.Companion.TRACK

class SearchActivity : AppCompatActivity() {
    private var binding: ActivitySearchBinding? = null
    private var viewModel: SearchViewModel? = null
    private var querySearchText = ""
    private var trackAdapter = TrackAdapter { startAdapter(it) }
    private var historyAdapter = TrackAdapter { startAdapter(it) }
    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        // Настройка Toolbar
        setSupportActionBar(binding?.toolbar)
        supportActionBar?.apply {
            title = getString(R.string.search)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        // Инициализация ViewModel
        viewModel = ViewModelProvider(
            this,
            SearchViewModel.getViewModelFactory(this)
        )[SearchViewModel::class.java]
        viewModel?.getSearchTrackStatusLiveData()?.observe(this) {
            render(it)
        }

        // Recycler View
        binding?.rvSearchTrack?.adapter = trackAdapter
        binding?.rvTracksHistory?.adapter = historyAdapter

        // кнопка очистить поиск
        binding?.ivClearButton?.setOnClickListener {
            viewModel?.clearSearchText()
            binding?.etButtonSearch?.text?.clear()
            hideImageView()
            val view = this.currentFocus
            if (view != null) {
                val inputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }

        // кнопка очистить историю поиска
        binding?.buttonClearHistory?.setOnClickListener {
            viewModel?.clearHistory()
        }

        // кнопка обновить поиск
        binding?.buttonUpdate?.setOnClickListener {
            viewModel?.search(querySearchText)
        }

        // фокусирование на вводе текста
        binding?.etButtonSearch?.requestFocus()

        // читать текст ввода
        binding?.etButtonSearch?.doOnTextChanged { text, _, _, _ ->
            binding?.ivClearButton?.visibility = clearButtonVisibility(text)
            text?.let {
                viewModel?.searchDebounce(it.toString())
                querySearchText = it.toString()
            }
        }
    }

    // Сохранение строки для одного цикла жизни
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_QUERY, querySearchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        querySearchText = savedInstanceState.getString(SEARCH_QUERY, "")
        if (querySearchText.isNotEmpty()) {
            binding?.etButtonSearch?.setText(querySearchText)
            viewModel?.search(querySearchText)
        }
    }

    private fun render(state: StateSearch) {
        when (state) {
            is StateSearch.Loading -> showLoading()
            is StateSearch.SearchHistory -> showHistoryList(state.tracks)
            is StateSearch.SearchResult -> showSearchResult(state.tracks)
            is StateSearch.SearchError -> showErrorMessage(state.error)
        }
    }

    private fun showHistoryList(tracksList: List<Track>) {
        hideImageView()
        historyAdapter.tracksList = tracksList as ArrayList
        if (historyAdapter.tracksList.isNotEmpty()) {
            binding?.llSearchHistory?.isVisible = true
            binding?.tvTittleHistory?.isVisible = true
            binding?.buttonClearHistory?.isVisible = true
        }
    }

    private fun showLoading() {
        hideImageView()
        binding?.progressBar?.isVisible = true
    }

    private fun showSearchResult(tracksList: List<Track>) {
        hideImageView()
        trackAdapter.clearTracks()
        trackAdapter.tracksList = tracksList as ArrayList
        binding?.rvSearchTrack?.isVisible = true
    }

    private fun showErrorMessage(error: NetworkError) {
        hideImageView()
        when (error) {
            NetworkError.NOTHING_FOUND -> {
                binding?.rvSearchTrack?.isVisible = false
                binding?.placeholder?.isVisible = false
                binding?.progressBar?.isVisible = false
                binding?.ivNothingFoundImage?.isVisible = true
                binding?.tvNothingFound?.isVisible = true
            }

            NetworkError.NO_CONNECTION -> {
                binding?.rvSearchTrack?.isVisible = false
                binding?.ivNothingFoundImage?.isVisible = false
                binding?.tvNothingFound?.isVisible = false
                binding?.llSearchHistory?.isVisible = false
                binding?.progressBar?.isVisible = false
                binding?.placeholder?.isVisible = true
                binding?.ivNoConnectionImage?.isVisible = true
                binding?.tvNoConnection?.isVisible = true
                binding?.buttonUpdate?.isVisible = true
            }
        }
    }

    private fun hideImageView() {
        binding?.ivNothingFoundImage?.isVisible = false
        binding?.tvNothingFound?.isVisible = false
        binding?.ivNoConnectionImage?.isVisible = false
        binding?.tvNoConnection?.isVisible = false
        binding?.buttonUpdate?.isVisible = false
        binding?.llSearchHistory?.isVisible = false
        binding?.progressBar?.isVisible = false
        binding?.rvSearchTrack?.isVisible = false
    }

    private fun clearButtonVisibility(char: CharSequence?): Int {
        return if (char.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun startAdapter(track: Track) {
        if (clickDebounce()) {
            viewModel?.addTrackInHistoryList(track)
            val intent = Intent(this, PlayerActivity::class.java)
                .apply { putExtra(TRACK, track) }
            clickDebounce()
            startActivity(intent)
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true },
                CLICK_DEBOUNCE_DELAY_MILLIS
            )
        }
        return current
    }

    companion object {
        private const val SEARCH_QUERY = "search_query"
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 1000L
    }
}