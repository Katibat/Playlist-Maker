package com.example.playlistmaker

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.*
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.*
import com.example.playlistmaker.track.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    companion object {
        const val SEARCH_EDIT_TEXT = "SEARCH_EDIT_TEXT"
        const val itunesBaseUrl = "https://itunes.apple.com"
    }

    private var text: String = ""
    private val tracksList = ArrayList<Track>()
    private var historyList = ArrayList<Track>()
    private var trackAdapter: TrackAdapter? = null
    private val interceptor = HttpLoggingInterceptor()
    private lateinit var searchEditText: EditText
    private var placeholder: LinearLayout? = null
    private var placeholderNoConnection: ImageView? = null
    private var placeholderNothingFound: ImageView? = null
    private var placeholderError: TextView? = null
    private var updateButton: Button? = null
    private var tittleHistory: TextView? = null
    private var buttonClearHistory: Button? = null

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
        setContentView(R.layout.activity_search)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        searchEditText = findViewById(R.id.buttonSearch)
        val clearButton = findViewById<ImageView>(R.id.ivClearButton)
        val recyclerView = findViewById<RecyclerView>(R.id.rvTracks)
        placeholder = findViewById(R.id.placeholder)
        placeholderNoConnection = findViewById(R.id.ivNoConnectionImage)
        placeholderNothingFound = findViewById(R.id.ivNothingFoundImage)
        placeholderError = findViewById(R.id.tvErrorMessage)
        updateButton = findViewById(R.id.buttonUpdate)
        tittleHistory = findViewById(R.id.tvTittleHistory)
        buttonClearHistory = findViewById(R.id.buttonClearHistory)

        // Recycler View
        trackAdapter = TrackAdapter()
        recyclerView.adapter = trackAdapter
        trackAdapter!!.tracksList = tracksList
        historyList.clear()
        historyList = SearchHistory.getHistory()

        // OkHTTP
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        // Настроить Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = getString(R.string.search)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        // фокусирование на вводе текста
        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            focusVisibility(hasFocus)
        }

        // найти track по введенному пользователем тексту
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
                true
            }
            false
        }

        // кнопка очистить поиск
        clearButton.setOnClickListener {
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(searchEditText.windowToken, 0)
            searchEditText.setText("")
            tracksList.clear()
            placeholder?.visibility = View.GONE
            showHistory()
            trackAdapter?.notifyDataSetChanged()
        }

        // кнопка обновить поиск
        updateButton?.setOnClickListener {
            search()
        }

        // кнопка очистить историю поиска
        buttonClearHistory?.setOnClickListener {
            SearchHistory.clearHistoryList()
            historyList.clear()
            goneHistoryButtons()
            trackAdapter?.notifyDataSetChanged()
        }

        // читать текст ввода
        val simpleTextWatcher = searchEditText?.doOnTextChanged { text, _, _, _ ->
            this@SearchActivity.text = text.toString()
            if (!text.isNullOrEmpty()) {
                clearButton.visibility = View.VISIBLE
                goneHistoryButtons()
                trackAdapter?.tracksList = tracksList
            } else {
                clearButton.visibility = View.GONE
            }
        }
        searchEditText.addTextChangedListener(simpleTextWatcher)
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
            placeholder?.visibility = View.VISIBLE
            placeholderNoConnection?.visibility = View.GONE
            placeholderNothingFound?.visibility = View.VISIBLE
            tracksList.clear()
            trackAdapter?.notifyDataSetChanged()
            placeholderError?.text = text
            if (additionalMessage.isNotEmpty()) {
                placeholderNothingFound?.visibility = View.GONE
                placeholderNoConnection?.visibility = View.VISIBLE
                updateButton?.visibility = View.VISIBLE
            } else {
                updateButton?.visibility = View.GONE
            }
        } else {
            placeholder?.visibility = View.GONE
        }
    }

    private fun search() {
        if (searchEditText.text.isNotEmpty()) {
            tracksService.search(searchEditText.text.toString()).enqueue(object :
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
        tittleHistory?.visibility = View.GONE
        buttonClearHistory?.visibility = View.GONE
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showHistory() {
        tittleHistory?.visibility = View.VISIBLE
        buttonClearHistory?.visibility = View.VISIBLE
        historyList = SearchHistory.getHistory()
        trackAdapter?.tracksList = historyList
        trackAdapter?.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun focusVisibility(hasFocus: Boolean) {
        if (hasFocus && searchEditText.text?.isEmpty()!! && historyList.isNotEmpty()) {
            tittleHistory?.visibility = View.VISIBLE
            buttonClearHistory?.visibility = View.VISIBLE
        } else {
            tittleHistory?.visibility = View.GONE
            buttonClearHistory?.visibility = View.GONE
        }
        trackAdapter?.tracksList = historyList
        trackAdapter?.notifyDataSetChanged()
    }
}