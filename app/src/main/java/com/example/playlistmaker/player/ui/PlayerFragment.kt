package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentAudioplayerBinding
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.media.util.PlaylistResult
import com.example.playlistmaker.player.domain.util.StatePlayer
import com.example.playlistmaker.playlist.ui.BackNavigationListenerPlayer
import com.example.playlistmaker.playlist.ui.PlaylistsAdapterBottomSheet
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.App.Companion.getTrackTimeMillis
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerFragment : Fragment(), BackNavigationListenerPlayer {
    private var _binding: FragmentAudioplayerBinding? = null
    private val binding get() = _binding!!
    private var url: String? = null
    private val viewModel by viewModel<PlayerViewModel>() {
        parametersOf(url)
    }
    private var adapter: PlaylistsAdapterBottomSheet? = null
    private var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>? = null
    private var track: Track? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAudioplayerBinding.inflate(inflater, container, false)
        binding.buttonCreateNewPlaylist.setOnClickListener {
            findNavController().navigate(R.id.action_mediaFragment_to_playlistCreateFragment)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        track = Gson().fromJson(requireArguments().getString(ARGS_TRACK), Track::class.java)

        track?.previewUrl?.let { viewModel.prepare(it) }
        showTrack(track!!)

        observeLiveData()
        setupUIComponents(track!!)
        setupBottomSheetBehaviorCallback()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun showTrack(track: Track) {
        binding.apply {
            tvTittleTrackName.text = track.trackName
            tvTittleTrackArtist.text = track.artistName
            tvDurationContent.text = getTrackTimeMillis(track.trackTimeMillis)
            if (track.collectionName.isEmpty() == true) {
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

    private fun setupClickListeners() {
        binding.ivPlayTrack.setOnClickListener {
            viewModel.changePlayerState()
        }

        binding.ivLikeTrack.setOnClickListener { viewModel.onFavoriteClicked(track!!) }

        binding.ivAddPlaylist.setOnClickListener {
//            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
            binding.dimOverlay.isVisible = true
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
            viewModel.getListOfPlaylist()
            viewModel.observePlaylistLiveData().observe(viewLifecycleOwner) { list ->
                adapter?.updateData(list)
                binding.rvBottomSheet.adapter = adapter
            }
        }

        binding.buttonCreateNewPlaylist.setOnClickListener {
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
            binding.svPlayer.isVisible = false
            binding.fcvContainerPlayer.isVisible = true
            findNavController().navigate(R.id.action_playerFragment_to_playlistCreateFragment)
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
        val dimOverlay: View = binding.dimOverlay

        bottomSheetBehavior?.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        dimOverlay.alpha = 1f
                        dimOverlay.isVisible = true
                    }

                    else -> {
                        dimOverlay.isVisible = false
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                dimOverlay.alpha = slideOffset
            }
        })
    }

    private suspend fun setupBottomSheet() {
        val bottomSheetContainer = binding.llBottomSheet
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }
        binding.rvBottomSheet.adapter = adapter

        adapter = PlaylistsAdapterBottomSheet(
            mutableListOf(),
            object : PlaylistsAdapterBottomSheet.Listener {
                override fun onClick(playlist: Playlist) {
                    lifecycleScope.launch {
                        track?.let { viewModel.addTrackToPlaylist(playlist, it) }
                    }
                }
            })

        binding.rvBottomSheet.adapter = adapter
        binding.buttonCreateNewPlaylist.isVisible = true
        binding.rvBottomSheet.isVisible = true
    }

    private fun observeLiveData() {
        track?.trackId?.let { viewModel.checkIsFavorite(it) }

        viewModel.observeFavoriteTracks().observe(viewLifecycleOwner) { isFavorite ->
            setLikeIcon(isFavorite)
        }

        viewModel.observeStatePlayer().observe(viewLifecycleOwner) { state ->
            when (state) {
                StatePlayer.PAUSED -> setPlayIcon()
                StatePlayer.PLAYING -> setPauseIcon()
                StatePlayer.PREPARED, StatePlayer.DEFAULT -> {
                    setPlayIcon()
                    binding.tvDurationTrack.text = getString(R.string.player_start_play_time)
                }
                else -> {}
            }
        }

        viewModel.observeCurrentTime().observe(viewLifecycleOwner) { time ->
            binding.tvDurationTrack.text = getTrackTimeMillis(time)
        }

        viewModel.observeTrackInPlaylistLiveData().observe(viewLifecycleOwner) { status ->
            showToastOnResultOfAddingTrack(status)
        }

        viewModel.observePlaylistLiveData().observe(viewLifecycleOwner) { playlists ->
            adapter?.updateData(playlists)
        }

        viewModel.observeListOfPlaylistsLiveData().observe(viewLifecycleOwner) { playlists ->
            adapter?.updateData(playlists)
        }
    }

    private fun showToastOnResultOfAddingTrack(status: PlaylistResult?) {
        when (status) {
            is PlaylistResult.Canceled -> {
                Toast.makeText(
                    requireContext(),
                    "Трек уже добавлен в плейлист ${status.playlist.name}",
                    Toast.LENGTH_SHORT
                ).show()
            }

            is PlaylistResult.Success -> {
                Toast.makeText(
                    requireContext(),
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
        findNavController().navigateUp()
    }

    private fun setPlayIcon() {
        binding.ivPlayTrack.setImageResource(R.drawable.audio_player_play)
    }

    private fun setPauseIcon() {
        binding.ivPlayTrack.setImageResource(R.drawable.audio_player_pause)
    }

    private fun setLikeIcon(isFavorite: Boolean) {
        if (isFavorite) {
            binding.ivLikeTrack.setImageResource(R.drawable.audio_player_like_favorite)
        } else {
            binding.ivLikeTrack.setImageResource(R.drawable.audio_player_like)
        }
    }

    companion object {
        const val ARGS_TRACK = "track"

        fun createArgs(track: String): Bundle =
            bundleOf(ARGS_TRACK to track)
    }
}