package com.example.playlistmaker.media.ui.favorite

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.MediaFragmentFavoriteTracksBinding
import com.example.playlistmaker.media.util.FavoriteTrackState
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.TrackAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaFragmentFavoriteTracks : Fragment() {

    private var _binding: MediaFragmentFavoriteTracksBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<MediaFavoriteTracksViewModel>()
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
                with(binding) {
                    progressBar.isVisible = true
                    ivPlaceholder.isVisible = false
                    tvError.isVisible = false
                    rvFavouriteTracks.isVisible = false
                }
            }

            is FavoriteTrackState.Content -> {
                favoriteTrackAdapter.clearTracks()
                favoriteTrackAdapter.tracksList = state.tracks as MutableList<Track>
                favoriteTrackAdapter.notifyDataSetChanged()
                with(binding) {
                    rvFavouriteTracks.isVisible = true
                    ivPlaceholder.isVisible = false
                    tvError.isVisible = false
                    progressBar.isVisible = false
                }
            }

            is FavoriteTrackState.Empty -> {
                with(binding) {
                    ivPlaceholder.isVisible = true
                    tvError.isVisible = true
                    rvFavouriteTracks.isVisible = false
                    progressBar.isVisible = false
                }
            }
        }
    }

    companion object {
        fun newInstance() = MediaFragmentFavoriteTracks()
    }
}