package com.example.playlistmaker.player.presentation

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityAudioplayerBinding
import com.example.playlistmaker.player.Creator
import com.example.playlistmaker.player.domain.api.AudioPlayerInteractor
import com.example.playlistmaker.player.domain.models.Track
import com.example.playlistmaker.player.domain.models.Track.Companion.TRACK
import com.example.playlistmaker.utils.App
import com.example.playlistmaker.utils.App.Companion.getTrackTimeMillis
import com.example.playlistmaker.utils.State
import java.util.*

class AudioPlayerActivity : AppCompatActivity() {
    private var binding: ActivityAudioplayerBinding? = null
    private val handler = Handler(Looper.getMainLooper())
    private val runnable = createUpdateTimerTrack()
    private val interactor: AudioPlayerInteractor = Creator.provideAudioPlayerInteractor()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioplayerBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        // Настроить Toolbar
        setSupportActionBar(binding?.toolbar)
        supportActionBar?.apply {
            title = ""
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        val track =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(TRACK, Track::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(TRACK)
            } as Track

        if (track.previewUrl?.isNotEmpty() == true) {
            preparePlayer(track.previewUrl)
        }

        binding?.ivPlayTrack?.setOnClickListener {
            interactor.switchedStatePlayer { state ->
                when (state) {
                    State.STATE_PAUSED, State.STATE_PREPARED, State.STATE_DEFAULT -> {
                        handler.removeCallbacks(runnable)
                        setPlayIcon()
                    }

                    State.STATE_PLAYING -> {
                        handler.removeCallbacks(runnable)
                        setPauseIcon()
                        handler.post(runnable)
                    }
                }
            }
        }

        binding?.tvDurationTrack?.setText(R.string.player_start_play_time)
        showTrack(track)
    }

    private fun showTrack(track: Track) {
        binding?.apply {
            tvTittleTrackName.text = track.trackName
            tvTittleTrackArtist.text = track.artistName
            tvDurationContent.text = getTrackTimeMillis(track.trackTimeMillis)
            if (track.collectionName.isEmpty()) {
                tvAlbumTittle.isVisible = false
                tvAlbumContent.isVisible = false
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

    override fun onPause() {
        super.onPause()
        interactor.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun preparePlayer(url: String) {
        interactor.preparePlayer(url) { state ->
            when (state) {
                State.STATE_PREPARED -> {
                    handler.removeCallbacks(runnable)
                    setPlayIcon()
                    binding?.tvDurationTrack?.setText(R.string.player_start_play_time)
                }

                else -> {
                    binding?.tvDurationTrack?.setText(R.string.player_start_play_time)
                    handler.removeCallbacks(runnable)
                    setPauseIcon()
                }
            }
        }
    }

    private fun createUpdateTimerTrack(): Runnable {
        return object : Runnable {
            override fun run() {
                binding?.tvDurationTrack?.text =
                    getTrackTimeMillis(interactor.getPosition())
                handler.postDelayed(this, DELAY_MILLIS)
            }
        }
    }

    private fun setPlayIcon() {
        if (!(applicationContext as App).darkTheme) {
            binding?.ivPlayTrack?.setImageResource(R.drawable.audio_player_play)
        } else {
            binding?.ivPlayTrack?.setImageResource(R.drawable.audio_player_play_dark)
        }
    }

    private fun setPauseIcon() {
        if (!(applicationContext as App).darkTheme) {
            binding?.ivPlayTrack?.setImageResource(R.drawable.audio_player_pause)
        } else {
            binding?.ivPlayTrack?.setImageResource(R.drawable.audio_player_pause_dark)
        }
    }

    companion object {
        private const val DELAY_MILLIS = 1000L
    }
}