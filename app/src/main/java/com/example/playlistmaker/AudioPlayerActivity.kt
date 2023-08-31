package com.example.playlistmaker

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.example.playlistmaker.track.Track
import com.example.playlistmaker.track.Track.Companion.TRACK
import java.util.*

@Suppress("DEPRECATION")
class AudioPlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAudioPlayerBinding

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Настройка Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = ""
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        val track = intent.getParcelableExtra<Track>(TRACK)!!
        playTrack(track)
    }

    private fun playTrack(track: Track) = with(binding) {
        tvTittleTrackName.text = track.trackName
        tvTittleTrackArtist.text = track.artistName
        tvDurationTrack.text = "00:30"
        tvDurationContent.text = track.getTrackTimeMillis()
        if (track.collectionName.isEmpty()) {
            tvAlbumTittle.visibility = View.GONE
            tvAlbumContent.visibility = View.GONE
        } else {
            tvAlbumContent.text = track.collectionName
        }

        tvYearContent.text = track.releaseDate.take(4)
        tvGenreContent.text = track.primaryGenreName
        tvCountryContent.text = track.country
        Glide.with(ivImagePlayer)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(2))
            .into(ivImagePlayer)
    }
}