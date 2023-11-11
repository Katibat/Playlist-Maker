package com.example.playlistmaker.media.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityMediaBinding
import com.google.android.material.tabs.TabLayoutMediator

class MediaActivity : AppCompatActivity() {
    private var binding: ActivityMediaBinding? = null
    private var tabMediator: TabLayoutMediator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        // Настройка Toolbar
        setSupportActionBar(binding?.toolbar)
        supportActionBar?.apply {
            title = getString(R.string.media)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        binding?.viewPager?.adapter = MediaViewPagerAdapter(supportFragmentManager, lifecycle)

        tabMediator =
            binding?.tabLayout?.let {
                binding?.viewPager?.let { it1 ->
                    TabLayoutMediator(it, it1) { tab, position ->
                        when(position) {
                            0 -> tab.text = getString(R.string.media_favorite_tracks_title)
                            1 -> tab.text = getString(R.string.media_playlist_title)
                        }
                    }
                }
            }
        tabMediator?.attach()
    }

    override fun onDestroy() {
        super.onDestroy()
        tabMediator?.detach()
    }
}