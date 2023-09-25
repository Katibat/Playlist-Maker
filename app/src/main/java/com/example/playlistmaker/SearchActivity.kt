package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.*
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.*
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.player.data.dto.TracksResponse
import com.example.playlistmaker.player.data.network.RetrofitNetworkClient.Companion.IMDB_BASE_URL
import com.example.playlistmaker.player.data.network.TracksApi
import com.example.playlistmaker.player.domain.models.Track
import com.example.playlistmaker.player.domain.models.Track.Companion.TRACK
import com.example.playlistmaker.player.presentation.AudioPlayerActivity
import com.example.playlistmaker.player.presentation.TrackAdapter
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    private var binding: ActivitySearchBinding? = null
    private var text: String = ""
    private val tracksList = ArrayList<Track>()
    private var historyList = ArrayList<Track>()
    private var trackAdapter: TrackAdapter? = null
    private val interceptor = HttpLoggingInterceptor()

    private val client = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(IMDB_BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val tracksService = retrofit.create(TracksApi::class.java)

    // Разрешить пользователю нажимать на элементы списка не чаще одного раза в секунду
    private val searchRunnable = Runnable { search() }
    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        trackAdapter = TrackAdapter {
            SearchHistory.addTrackInHistoryList(it)
            if (clickDebounce()) {
                val intent = Intent(this, AudioPlayerActivity::class.java)
                    .apply { putExtra(TRACK, it) }
                startActivity(intent)
            }
        }

        // Recycler View
        trackAdapter?.tracksList = tracksList
        binding?.rvTracks?.adapter = trackAdapter
        historyList.clear()
        historyList = SearchHistory.getHistory()

        // OkHTTP
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        // Настройка Toolbar
        setSupportActionBar(binding?.toolbar)
        supportActionBar?.apply {
            title = getString(R.string.search)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        // фокусирование на вводе текста
        binding?.buttonSearch?.setOnFocusChangeListener { _, hasFocus ->
            focusVisibility(hasFocus)
        }

        // найти track по введенному пользователем тексту
        binding?.buttonSearch?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
                true
            }
            false
        }

        // кнопка очистить поиск
        binding?.ivClearButton?.setOnClickListener {
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(binding?.buttonSearch?.windowToken, 0)
            binding?.buttonSearch?.setText("")
            tracksList.clear()
            binding?.placeholder?.isVisible = false
            showHistory()
            trackAdapter?.notifyDataSetChanged()
        }

        // кнопка обновить поиск
        binding?.buttonUpdate?.setOnClickListener {
            search()
        }

        // кнопка очистить историю поиска
        binding?.buttonClearHistory?.setOnClickListener {
            SearchHistory.clearHistoryList()
            historyList.clear()
            goneHistoryButtons()
            trackAdapter?.notifyDataSetChanged()
        }

        // читать текст ввода
        val simpleTextWatcher = binding?.buttonSearch?.doOnTextChanged { text, _, _, _ ->
            this@SearchActivity.text = text.toString()
            if (!text.isNullOrEmpty()) {
                binding?.ivClearButton?.isVisible = true
                searchDebounce()
                goneHistoryButtons()
                trackAdapter?.tracksList = tracksList
            } else {
                binding?.ivClearButton?.isVisible = false
            }
        }

        binding?.buttonSearch?.addTextChangedListener(simpleTextWatcher)
    }

    // Сохранение строки для одного цикла жизни
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_EDIT_TEXT, text)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        text = savedInstanceState.getString(SEARCH_EDIT_TEXT).toString()
        binding?.buttonSearch?.setText(text)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showMessage(text: String, additionalMessage: String) {
        if (text.isNotEmpty()) {
            binding?.placeholder?.isVisible = true
            binding?.ivNoConnectionImage?.isVisible = false
            binding?.ivNothingFoundImage?.isVisible = true
            tracksList.clear()
            trackAdapter?.notifyDataSetChanged()
            binding?.tvErrorMessage?.text = text
            if (additionalMessage.isNotEmpty()) {
                binding?.ivNothingFoundImage?.isVisible = false
                binding?.ivNoConnectionImage?.isVisible = true
                binding?.buttonUpdate?.isVisible = true
            } else {
                binding?.buttonUpdate?.isVisible = false
            }
        } else {
            binding?.placeholder?.isVisible = false
        }
    }

    private fun search() {
        if (binding?.buttonSearch?.text?.isNotEmpty() == true) {
            binding?.progressBar?.isVisible = true
            binding?.rvTracks?.isVisible = false
            binding?.ivNothingFoundImage?.isVisible = false
            tracksService.search(binding?.buttonSearch?.text.toString())
                .enqueue(object : Callback<TracksResponse> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<TracksResponse>,
                    response: Response<TracksResponse>
                ) {
                    if (response.code() == 200) {
                        binding?.progressBar?.isVisible = false
                        binding?.rvTracks?.visibility = View.VISIBLE
                        tracksList.clear()
                    }
                    if (response.body()?.results?.isNotEmpty() == true) {
                        binding?.rvTracks?.isVisible = true
                        tracksList.addAll(response.body()?.results!!)
                        trackAdapter?.notifyDataSetChanged()
                    }
                    if (tracksList.isEmpty()) {
                        showMessage(getString(R.string.nothing_found), "")
                        binding?.progressBar?.isVisible = false
                        binding?.ivNothingFoundImage?.isVisible = true
                    } else {
                        showMessage("", "")
                    }
                }

                override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                    showMessage(getString(R.string.something_went_wrong), t.message.toString())
                    binding?.progressBar?.isVisible = false
                }
            })
        }
    }

    private fun goneHistoryButtons() {
        binding?.tvTittleHistory?.isVisible = false
        binding?.buttonClearHistory?.isVisible = false
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showHistory() {
        if (tracksList.isEmpty()) {
            binding?.tvTittleHistory?.isVisible = false
            binding?.buttonUpdate?.isVisible = false
        } else {
            binding?.tvTittleHistory?.isVisible = true
            binding?.buttonClearHistory?.isVisible = true
            historyList = SearchHistory.getHistory()
            trackAdapter?.tracksList = historyList
            trackAdapter?.notifyDataSetChanged()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun focusVisibility(hasFocus: Boolean) {
        if (hasFocus && binding?.buttonSearch?.text?.isEmpty()!! && historyList.isNotEmpty()) {
            binding?.tvTittleHistory?.isVisible = true
            binding?.buttonClearHistory?.isVisible = true
        } else {
            binding?.tvTittleHistory?.isVisible = false
            binding?.buttonClearHistory?.isVisible = false
        }
        trackAdapter?.tracksList = historyList
        trackAdapter?.notifyDataSetChanged()
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY_MILLIS)
        }
        return current
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY_MILLIS)
    }

    companion object {
        private const val SEARCH_EDIT_TEXT = "SEARCH_EDIT_TEXT"
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 1000L
        private const val SEARCH_DEBOUNCE_DELAY_MILLIS = 2000L
    }
}