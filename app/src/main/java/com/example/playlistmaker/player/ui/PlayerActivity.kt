package com.example.playlistmaker.player.ui

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityAudioplayerBinding
import com.example.playlistmaker.player.domain.util.StatePlayer
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.models.Track.Companion.TRACK
import com.example.playlistmaker.utils.App
import com.example.playlistmaker.utils.App.Companion.getTrackTimeMillis
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

@Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
class PlayerActivity : AppCompatActivity() {
    private var binding: ActivityAudioplayerBinding? = null
    private val viewModel by viewModel<PlayerViewModel>()

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

        viewModel.observeCurrentTimeLiveData().observe(this) { time ->
            binding?.tvDurationTrack?.text = getTrackTimeMillis(time)
        }

        viewModel.observePlayerState().observe(this) { state ->
            when(state) {
                StatePlayer.PAUSED -> setPlayIcon()
                StatePlayer.PLAYING -> setPauseIcon()
                StatePlayer.PREPARED, StatePlayer.DEFAULT -> {
                    setPlayIcon()
                    binding?.tvDurationTrack?.text = getString(R.string.player_start_play_time)
                }
            }
        }

        track.previewUrl?.let { viewModel.prepare(it) }

        binding?.ivPlayTrack?.setOnClickListener {
            viewModel.changePlayerState()
        }

        showTrack(track)
    }

    private fun showTrack(track: Track) {
        binding?.apply {
            tvDurationTrack.text = getString(R.string.player_start_play_time)
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
                .transform(RoundedCorners(8))
                .into(ivImagePlayer as ImageView)
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun onStart() {
        super.onStart()
        viewModel.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onStop()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
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
}