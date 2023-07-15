package com.example.playlistmaker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class MediaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)
        val toolbar: Toolbar = findViewById(R.id.toolbar)

        // Настройка Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = getString(R.string.media)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
    }
}