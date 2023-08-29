package com.example.playlistmaker

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.*
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.*
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.track.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private var text: String = ""
    private val tracksList = ArrayList<Track>()
    private var historyList = ArrayList<Track>()
    private var trackAdapter: TrackAdapter? = null
    private val interceptor = HttpLoggingInterceptor()

    private val client = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val tracksService = retrofit.create(TracksApi::class.java)

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recycler View
        trackAdapter = TrackAdapter()
        binding.rvTracks.adapter = trackAdapter
        trackAdapter!!.tracksList = tracksList
        historyList.clear()
        historyList = SearchHistory.getHistory()

        // OkHTTP
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        // Настройка Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = getString(R.string.search)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        // фокусирование на вводе текста
        binding.buttonSearch.setOnFocusChangeListener { _, hasFocus ->
            focusVisibility(hasFocus)
        }

        // найти track по введенному пользователем тексту
        binding.buttonSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
                true
            }
            false
        }

        // кнопка очистить поиск
        binding.ivClearButton.setOnClickListener {
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(binding.buttonSearch.windowToken, 0)
            binding.buttonSearch.setText("")
            tracksList.clear()
            binding.placeholder.visibility = View.GONE
            showHistory()
            trackAdapter?.notifyDataSetChanged()
        }

        // кнопка обновить поиск
        binding.buttonUpdate.setOnClickListener {
            search()
        }

        // кнопка очистить историю поиска
        binding.buttonClearHistory.setOnClickListener {
            SearchHistory.clearHistoryList()
            historyList.clear()
            goneHistoryButtons()
            trackAdapter?.notifyDataSetChanged()
        }

        // читать текст ввода
        val simpleTextWatcher = binding.buttonSearch.doOnTextChanged { text, _, _, _ ->
            this@SearchActivity.text = text.toString()
            if (!text.isNullOrEmpty()) {
                binding.ivClearButton.visibility = View.VISIBLE
                goneHistoryButtons()
                trackAdapter?.tracksList = tracksList
            } else {
                binding.ivClearButton.visibility = View.GONE
            }
        }
        binding.buttonSearch.addTextChangedListener(simpleTextWatcher)
    }

    // Сохранение строки для одного цикла жизни
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val searchEditText = findViewById<EditText>(R.id.buttonSearch).text.toString()
        outState.putString(SEARCH_EDIT_TEXT, searchEditText)
        outState.putString(SEARCH_EDIT_TEXT, text)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        val searchEditText = findViewById<EditText>(R.id.buttonSearch)
        super.onRestoreInstanceState(savedInstanceState)
        text = savedInstanceState.getString(SEARCH_EDIT_TEXT).toString()
        searchEditText.setText(text)
    }

    // показать сообщение об ошибке
    @SuppressLint("NotifyDataSetChanged")
    private fun showMessage(text: String, additionalMessage: String) {
        if (text.isNotEmpty()) {
            binding.placeholder.visibility = View.VISIBLE
            binding.ivNoConnectionImage.visibility = View.GONE
            binding.ivNothingFoundImage.visibility = View.VISIBLE
            tracksList.clear()
            trackAdapter?.notifyDataSetChanged()
            binding.tvErrorMessage.text = text
            if (additionalMessage.isNotEmpty()) {
                binding.ivNothingFoundImage.visibility = View.GONE
                binding.ivNoConnectionImage.visibility = View.VISIBLE
                binding.buttonUpdate.visibility = View.VISIBLE
            } else {
                binding.buttonUpdate.visibility = View.GONE
            }
        } else {
            binding.placeholder.visibility = View.GONE
        }
    }

    private fun search() {
        if (binding.buttonSearch.text?.isNotEmpty() == true) {
            tracksService.search(binding.buttonSearch.text.toString()).enqueue(object :
                Callback<TracksResponse> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<TracksResponse>,
                    response: Response<TracksResponse>
                ) {
                    if (response.code() == 200) {
                        tracksList.clear()
                        if (response.body()?.results?.isNotEmpty() == true) {
                            tracksList.addAll(response.body()?.results!!)
                            trackAdapter?.notifyDataSetChanged()
                        }
                        if (tracksList.isEmpty()) {
                            showMessage(getString(R.string.nothing_found), "")
                        } else {
                            showMessage("", "")
                        }
                    } else {
                        showMessage(
                            getString(R.string.something_went_wrong),
                            response.code().toString()
                        )
                    }
                }

                override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                    showMessage(getString(R.string.something_went_wrong), t.message.toString())
                }

            })
        }
    }

    private fun goneHistoryButtons() {
        binding.tvTittleHistory.visibility = View.GONE
        binding.buttonClearHistory.visibility = View.GONE
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showHistory() {
        binding.tvTittleHistory.visibility = View.VISIBLE
        binding.buttonClearHistory.visibility = View.VISIBLE
        historyList = SearchHistory.getHistory()
        trackAdapter?.tracksList = historyList
        trackAdapter?.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun focusVisibility(hasFocus: Boolean) {
        if (hasFocus && binding.buttonSearch.text?.isEmpty()!! && historyList.isNotEmpty()) {
            binding.tvTittleHistory.visibility = View.VISIBLE
            binding.buttonClearHistory.visibility = View.VISIBLE
        } else {
            binding.tvTittleHistory.visibility = View.GONE
            binding.buttonClearHistory.visibility = View.GONE
        }
        trackAdapter?.tracksList = historyList
        trackAdapter?.notifyDataSetChanged()
    }

    companion object {
        const val SEARCH_EDIT_TEXT = "SEARCH_EDIT_TEXT"
        const val itunesBaseUrl = "https://itunes.apple.com"
    }
}