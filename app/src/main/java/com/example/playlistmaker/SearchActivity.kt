package com.example.playlistmaker

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class SearchActivity : AppCompatActivity() {
    private var text: String = ""

    companion object {
        const val SEARCH_EDIT_TEXT="SEARCH_EDIT_TEXT"
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        val clearButton = findViewById<ImageView>(R.id.clearButton)
        val searchEditText = findViewById<EditText>(R.id.search)

        // Настроить Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = getString(R.string.settings)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        clearButton.setOnClickListener {
            val inputMethodManager =
                getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(searchEditText.windowToken, 0)
            searchEditText.setText("")
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                text = searchEditText.text.toString()
                visibilityItem(s)
            }

            private fun visibilityItem(s: CharSequence?) {
                if (!s.isNullOrEmpty()) {
                    clearButton.visibility = View.VISIBLE
                } else {
                    clearButton.visibility = View.GONE
                }
            }
            override fun afterTextChanged(s: Editable?) {
            }
        }
        searchEditText.addTextChangedListener(simpleTextWatcher)
    }

    // Сохранение строки
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val searchEditText = findViewById<EditText>(R.id.search).text.toString()
        outState.putString(SEARCH_EDIT_TEXT, searchEditText)
        outState.putString(SEARCH_EDIT_TEXT, text)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val searchEditText = findViewById<EditText>(R.id.search)
        super.onRestoreInstanceState(savedInstanceState)
        text = savedInstanceState.getString(SEARCH_EDIT_TEXT).toString()
        searchEditText.setText(text)
    }
}