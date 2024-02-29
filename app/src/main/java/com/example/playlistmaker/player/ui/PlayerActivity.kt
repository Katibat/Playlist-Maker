package com.example.playlistmaker.player.ui

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityAudioplayerBinding
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.media.util.PlaylistResult
import com.example.playlistmaker.player.domain.util.StatePlayer
import com.example.playlistmaker.playlist.ui.BackNavigationListenerPlayer
import com.example.playlistmaker.playlist.ui.PlaylistCreateFragment
import com.example.playlistmaker.playlist.ui.PlaylistsAdapterBottomSheet
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.models.Track.Companion.TRACK
import com.example.playlistmaker.utils.App.Companion.getTrackTimeMillis
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class PlayerActivity : AppCompatActivity(), BackNavigationListenerPlayer {
    private var binding: ActivityAudioplayerBinding? = null
    private val viewModel by viewModel<PlayerViewModel>()
    private var adapter: PlaylistsAdapterBottomSheet? = null
    private var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>? = null
    private var track: Track? = null

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

        track =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(TRACK, Track::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(TRACK)
            } as Track

        observeLiveData()
        setupUIComponents(track!!)
        setupBottomSheetBehaviorCallback()

        track?.previewUrl?.let { viewModel.prepare(it) }
    }

    private fun showTrack(track: Track) {
        binding?.apply {
            tvTittleTrackName.text = track.trackName
            tvTittleTrackArtist.text = track.artistName
            tvDurationContent.text = getTrackTimeMillis(track.trackTimeMillis)
            if (track.collectionName?.isEmpty() == true) {
                tvAlbumTittle.isVisible = false
                tvAlbumContent.isVisible = false
            } else {
                tvAlbumContent.text = track.collectionName
            }
            tvYearContent.text = track.releaseDate?.take(4)
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

    private fun setupClickListeners() {
        binding?.ivPlayTrack?.setOnClickListener {
            viewModel.changePlayerState()
        }

        binding?.ivLikeTrack?.setOnClickListener { viewModel.onFavoriteClicked(track = track!!) }

        binding?.ivAddPlaylist?.setOnClickListener {
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
            binding?.dimOverlay?.isVisible = true
        }

        binding?.buttonCreateNewPlaylist?.setOnClickListener {
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
            binding?.svPlayer?.isVisible = false
            binding?.fcvContainerPlayer?.isVisible = true
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fcvContainerPlayer, PlaylistCreateFragment())
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
    }

    private fun callFromFragment() {
        val fragmentManager = supportFragmentManager
        val fragment = fragmentManager.findFragmentById(R.id.fcvContainerPlayer)

        if (fragment != null) {
            fragmentManager.beginTransaction()
                .remove(fragment)
                .commit()
        }
        binding?.fcvContainerPlayer?.isVisible = false
        viewModel.getListOfPlaylist()
        binding?.svPlayer?.isVisible = true
    }

    private fun backCheckFragment() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fcvContainerPlayer)
        if (currentFragment is PlaylistCreateFragment) {
            lifecycleScope.launch {
                currentFragment.navigateBack()
            }
        } else {
            super.onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupUIComponents(track: Track) {
        lifecycleScope.launch {
            setupBottomSheet()
        }
        setupClickListeners()
        showTrack(track)
    }

    // dimOverlay
    private fun setupBottomSheetBehaviorCallback() {
        val dimOverlay: View? = binding?.dimOverlay

        bottomSheetBehavior?.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        dimOverlay?.alpha = 1f
                        dimOverlay?.isVisible = true
                    }

                    else -> {
                        dimOverlay?.isVisible = false
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                dimOverlay?.alpha = slideOffset
            }
        })
    }

    private suspend fun setupBottomSheet() {
        val bottomSheetContainer = binding?.llBottomSheet
        bottomSheetBehavior = bottomSheetContainer?.let { BottomSheetBehavior.from(it) }
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
        binding?.rvBottomSheet?.layoutManager = LinearLayoutManager(this)

        adapter = PlaylistsAdapterBottomSheet(
            this,
            mutableListOf(),
            object : PlaylistsAdapterBottomSheet.Listener {
                override fun onClick(playlist: Playlist) {
                    lifecycleScope.launch {
                        track?.let { viewModel.addTrackToPlaylist(playlist, it) }
                    }
                }
            })

        binding?.rvBottomSheet?.adapter = adapter

    }

    private fun observeLiveData() {
        track?.trackId?.let { viewModel.checkIsFavorite(it) }

        viewModel.observeFavoriteTracks().observe(this) { isFavorite ->
            setLikeIcon(isFavorite)
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

        viewModel.observeTrackInPlaylistLiveData().observe(this) { status ->
            showToastOnResultOfAddingTrack(status)
        }

        viewModel.observePlaylistLiveData().observe(this) { playlists ->
            adapter?.updateData(playlists)
        }

        viewModel.observeListOfPlaylistsLiveData().observe(this) { playlists ->
            adapter?.updateData(playlists)
        }
    }

    private fun showToastOnResultOfAddingTrack(status: PlaylistResult?) {
        when (status) {
            is PlaylistResult.Canceled -> {
                Toast.makeText(
                    this,
                    "Трек уже добавлен в плейлист ${status.playlist.name}",
                    Toast.LENGTH_SHORT
                ).show()
            }

            is PlaylistResult.Success -> {
                Toast.makeText(
                    this,
                    "Добавлено в плейлист ${status.playlist.name}",
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {}
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

    override fun onNavigateBack(isEmpty: Boolean) {
        if (isEmpty) callFromFragment()
        else backCheckFragment()
    }

    private fun setPlayIcon() {
        binding?.ivPlayTrack?.setImageResource(R.drawable.audio_player_play)
    }

    private fun setPauseIcon() {
        binding?.ivPlayTrack?.setImageResource(R.drawable.audio_player_pause)
    }

    private fun setLikeIcon(isFavorite: Boolean) {
        if (isFavorite) {
            binding?.ivLikeTrack?.setImageResource(R.drawable.audio_player_like_favorite)
        } else {
            binding?.ivLikeTrack?.setImageResource(R.drawable.audio_player_like)
        }
    }
}