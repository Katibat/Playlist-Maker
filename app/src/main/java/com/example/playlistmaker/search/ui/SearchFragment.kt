package com.example.playlistmaker.search.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.player.ui.PlayerFragment
import com.example.playlistmaker.search.domain.models.NetworkError
import com.example.playlistmaker.player.domain.models.Track
import com.example.playlistmaker.player.ui.TrackAdapter
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment(), TrackAdapter.OnItemClickListener, TrackAdapter.OnItemLongClickListener {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<SearchViewModel>()
    private var querySearchText = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Получить актуальное состояние из LiveData()
        viewModel.observeSearchState().observe(viewLifecycleOwner) {
            render(it)
        }

        // кнопка очистить поиск
        binding.ivClearButton.setOnClickListener {
            viewModel.clearSearchText()
            binding.etButtonSearch.text?.clear()
            hideImageView()
            val inputMethodManager = requireActivity().getSystemService(
                AppCompatActivity.INPUT_METHOD_SERVICE
            ) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }

        // кнопка очистить историю поиска
        binding.buttonClearHistory.setOnClickListener {
            viewModel.clearHistory()
        }

        // кнопка обновить поиск
        binding.buttonUpdate.setOnClickListener {
            viewModel.search(querySearchText)
        }

        // фокусирование на вводе текста
        binding.etButtonSearch.requestFocus()

        // читать текст ввода
        binding.etButtonSearch.doOnTextChanged { text, _, _, _ ->
            binding.ivClearButton.visibility = clearButtonVisibility(text)
            text?.let {
                viewModel.searchDebounce(it.toString())
                querySearchText = it.toString()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Сохранение строки для одного цикла жизни
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_QUERY, querySearchText)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            querySearchText = savedInstanceState.getString(SEARCH_QUERY, "")
        }
        if (querySearchText.isNotEmpty()) {
            binding.etButtonSearch.setText(querySearchText)
            viewModel.search(querySearchText)
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
        binding.rvTracksHistory.adapter = TrackAdapter(ArrayList(tracksList), this@SearchFragment, this@SearchFragment)
        if (tracksList.isNotEmpty()) {
            with(binding) {
                llSearchHistory.isVisible = true
                tvTittleHistory.isVisible = true
                buttonClearHistory.isVisible = true
            }
        }
    }

    private fun showLoading() {
        hideImageView()
        binding.progressBar.isVisible = true
    }

    private fun showSearchResult(tracksList: List<Track>) {
        hideImageView()
        binding.rvSearchTrack.adapter = TrackAdapter(ArrayList(tracksList), this@SearchFragment, this@SearchFragment)
        binding.rvSearchTrack.isVisible = true
    }

    private fun showErrorMessage(error: NetworkError) {
        hideImageView()
        when (error) {
            NetworkError.NOTHING_FOUND -> {
                with(binding) {
                    rvSearchTrack.isVisible = false
                    placeholder.isVisible = false
                    progressBar.isVisible = false
                    ivNothingFoundImage.isVisible = true
                    tvNothingFound.isVisible = true
                }
            }

            NetworkError.NO_CONNECTION -> {
                with(binding) {
                    rvSearchTrack.isVisible = false
                    ivNothingFoundImage.isVisible = false
                    tvNothingFound.isVisible = false
                    llSearchHistory.isVisible = false
                    progressBar.isVisible = false
                    placeholder.isVisible = true
                    ivNoConnectionImage.isVisible = true
                    tvNoConnection.isVisible = true
                    buttonUpdate.isVisible = true
                }
            }
        }
    }

    private fun hideImageView() {
        with(binding) {
            ivNothingFoundImage.isVisible = false
            tvNothingFound.isVisible = false
            ivNoConnectionImage.isVisible = false
            tvNoConnection.isVisible = false
            buttonUpdate.isVisible = false
            llSearchHistory.isVisible = false
            progressBar.isVisible = false
            rvSearchTrack.isVisible = false
        }
    }

    private fun clearButtonVisibility(char: CharSequence?): Int {
        return if (char.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    companion object {
        private const val SEARCH_QUERY = "search_query"
    }

    override fun onItemClick(track: Track) {
        if (viewModel.clickDebounce()) {
            viewModel.addTrackInHistoryList(track)
            val trackJson = Gson().toJson(track)
            findNavController().navigate(
                R.id.action_searchFragment_to_playerFragment,
                PlayerFragment.createArgs(trackJson)
            )
        }
    }

    override fun onItemLongClick(track: Track): Boolean {
        return true
    }
}