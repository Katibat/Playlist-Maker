package com.example.playlistmaker

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.App.Companion.getTrackTimeMillis
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.example.playlistmaker.track.Track
import com.example.playlistmaker.track.Track.Companion.TRACK
import java.util.*

class AudioPlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAudioPlayerBinding
    private var playerState = STATE_DEFAULT
    private var mediaPlayer = MediaPlayer()
    private val handler = Handler(Looper.getMainLooper())
    private val runnable = createUpdateTimerTrack()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Настроить Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = ""
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        val track = intent.getParcelableExtra<Track>(TRACK)!!
        if (track != null) { // обработать ошибку NullPointerException
            playTrack(track)
            if (track.previewUrl?.isNotEmpty() == true) {
                preparePlayer(track.previewUrl)
            }
        }

        binding.ivPlayTrack.setOnClickListener {
            playbackControl(track)
        }

        binding.tvDurationTrack.setText(R.string.player_start_play_time)
    }

    private fun playTrack(track: Track) = with(binding) {
        tvTittleTrackName.text = track.trackName
        tvTittleTrackArtist.text = track.artistName
        tvDurationContent.text = getTrackTimeMillis(track.trackTimeMillis)
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

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        handler.removeCallbacks(runnable)
    }

    private fun playbackControl(track: Track) {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer(track)
            }
        }
    }

    private fun preparePlayer(url: String) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            binding.ivPlayTrack.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            if (!(applicationContext as App).darkTheme) {
                binding.ivPlayTrack.setImageResource(R.drawable.audio_player_play)
            } else {
                binding.ivPlayTrack.setImageResource(R.drawable.audio_player_play_dark)
            }
            playerState = STATE_PREPARED
            binding.tvDurationTrack.setText(R.string.player_start_play_time)
            handler.removeCallbacks(runnable)
        }
    }

    private fun startPlayer(track: Track) {
        if (track.previewUrl?.isNotEmpty() == true) {
            mediaPlayer.start()
            if (!(applicationContext as App).darkTheme) {
            binding.ivPlayTrack.setImageResource(R.drawable.audio_player_pause)
            } else {
                binding.ivPlayTrack.setImageResource(R.drawable.audio_player_pause_dark)
            }
            playerState = STATE_PLAYING
            handler.post(runnable)
        }
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        if (!(applicationContext as App).darkTheme) {
            binding.ivPlayTrack.setImageResource(R.drawable.audio_player_play)
        } else {
            binding.ivPlayTrack.setImageResource(R.drawable.audio_player_play_dark)
        }
        playerState = STATE_PAUSED
        handler.removeCallbacks(runnable)
    }

    private fun createUpdateTimerTrack(): Runnable {
        return object : Runnable {
            override fun run() {
                binding.tvDurationTrack.text =
                    getTrackTimeMillis(mediaPlayer.currentPosition.toLong())
                handler.postDelayed(this, DELAY)
            }
        }
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val DELAY = 1000L
    }
}