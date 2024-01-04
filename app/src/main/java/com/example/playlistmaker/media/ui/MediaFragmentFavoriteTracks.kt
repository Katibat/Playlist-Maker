package com.example.playlistmaker.media.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.MediaFragmentFavoriteTracksBinding
import com.example.playlistmaker.db.ui.FavoriteTrackViewModel
import com.example.playlistmaker.db.util.FavoriteTrackState
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.TrackAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaFragmentFavoriteTracks : Fragment() {

    private var _binding: MediaFragmentFavoriteTracksBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<FavoriteTrackViewModel>()
    private val favoriteTrackAdapter by lazy { TrackAdapter { startAdapter(it) } }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MediaFragmentFavoriteTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Получить актуальное состояние из LiveData()
        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        // Recycler View
        binding.rvFavouriteTracks.adapter = favoriteTrackAdapter

        // get favorite Tracks from bd
        viewModel.fillData()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fillData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun startAdapter(track: Track) {
        if (viewModel.clickDebounce()) {
            val intent = Intent(requireContext(), PlayerActivity::class.java)
                .apply { putExtra(Track.TRACK, track) }
            viewModel.clickDebounce()
            startActivity(intent)
        }
    }

    private fun render(state: FavoriteTrackState) {
        when (state) {
            is FavoriteTrackState.Loading -> {
                binding.progressBar.isVisible = true
                binding.ivPlaceholder.isVisible = false
                binding.tvError.isVisible = false
                binding.rvFavouriteTracks.isVisible = false
            }

            is FavoriteTrackState.Content -> {
                favoriteTrackAdapter.clearTracks()
                favoriteTrackAdapter.tracksList = state.tracks as MutableList<Track>
                favoriteTrackAdapter.notifyDataSetChanged()
                binding.rvFavouriteTracks.isVisible = true
                binding.ivPlaceholder.isVisible = false
                binding.tvError.isVisible = false
                binding.progressBar.isVisible = false
            }

            is FavoriteTrackState.Empty -> {
                binding.ivPlaceholder.isVisible = true
                binding.tvError.isVisible = true
                binding.rvFavouriteTracks.isVisible = false
                binding.progressBar.isVisible = false
            }
        }
    }

    companion object {
        fun newInstance() = MediaFragmentFavoriteTracks()
    }
}