package com.example.playlistmaker.player.ui

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityAudioplayerBinding
import com.example.playlistmaker.player.domain.util.StatePlayer
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.models.Track.Companion.TRACK
import com.example.playlistmaker.utils.App
import com.example.playlistmaker.utils.App.Companion.getTrackTimeMillis
import kotlinx.coroutines.launch
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

       viewModel.checkIsFavorite(track.trackId)

        viewModel.observeFavorite().observe(this) { isFavorite ->
            if (isFavorite) {
                setLikeFavoriteIcon()
            } else {
                setLikeIcon()
            }
        }

        viewModel.observeStatePlayer().observe(this) { state ->
            when (state) {
                StatePlayer.PAUSED -> setPlayIcon()
                StatePlayer.PLAYING -> setPauseIcon()
                StatePlayer.PREPARED, StatePlayer.DEFAULT -> {
                    setPlayIcon()
                    binding?.tvDurationTrack?.text = getString(R.string.player_start_play_time)
                }
            }
        }

        viewModel.observeCurrentTime().observe(this) { time ->
            binding?.tvDurationTrack?.text = getTrackTimeMillis(time)
        }

        track.previewUrl?.let { viewModel.prepare(it) }

        binding?.ivPlayTrack?.setOnClickListener {
            viewModel.changePlayerState()
        }

        binding?.ivLikeTrack?.setOnClickListener { viewModel.onFavoriteClicked(track = track) }

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
                .transform(RoundedCorners(8))
                .into(ivImagePlayer)
        }
    }

    override fun onPause() {
        viewModel.onPause()
        super.onPause()
    }

    override fun onStart() {
        viewModel.onStart()
        super.onStart()
    }

    override fun onResume() {
        viewModel.onResume()
        super.onResume()
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

    private fun setLikeFavoriteIcon() {
        if (!(applicationContext as App).darkTheme) {
            binding?.ivLikeTrack?.setImageResource(R.drawable.audio_player_like_favorite_dark)
        } else {
            binding?.ivLikeTrack?.setImageResource(R.drawable.audio_player_like_favorite)
        }
    }

    private fun setLikeIcon() {
        if (!(applicationContext as App).darkTheme) {
            binding?.ivLikeTrack?.setImageResource(R.drawable.audio_player_like_dark)
        } else {
            binding?.ivLikeTrack?.setImageResource(R.drawable.audio_player_like)
        }
    }
}