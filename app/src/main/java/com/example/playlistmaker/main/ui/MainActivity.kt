package com.example.playlistmaker.main.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.media.ui.MediaActivity
import com.example.playlistmaker.databinding.ActivityMainBinding
import com.example.playlistmaker.search.ui.SearchActivity
import com.example.playlistmaker.settings.ui.SettingsActivity

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.buttonSearch?.setOnClickListener {
            val searchIntent = Intent(this, SearchActivity::class.java)
            searchIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(searchIntent)
        }

        binding?.buttonMedia?.setOnClickListener {
            val mediaIntent = Intent(this, MediaActivity::class.java)
            startActivity(mediaIntent)
        }

        binding?.buttonSettings?.setOnClickListener {
            val settingIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingIntent)
        }
    }
}