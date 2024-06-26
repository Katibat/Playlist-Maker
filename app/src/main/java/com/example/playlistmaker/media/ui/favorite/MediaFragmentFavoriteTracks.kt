package com.example.playlistmaker.media.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.MediaFragmentFavoriteTracksBinding
import com.example.playlistmaker.media.util.FavoriteTrackState
import com.example.playlistmaker.player.ui.PlayerFragment
import com.example.playlistmaker.player.domain.models.Track
import com.example.playlistmaker.player.ui.TrackAdapter
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaFragmentFavoriteTracks : Fragment(),
    TrackAdapter.OnItemClickListener, TrackAdapter.OnItemLongClickListener {

    private var _binding: MediaFragmentFavoriteTracksBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<MediaFavoriteTracksViewModel>()

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
                binding.rvFavouriteTracks.adapter = TrackAdapter(ArrayList(state.tracks),
                    this@MediaFragmentFavoriteTracks, this@MediaFragmentFavoriteTracks)
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

    override fun onItemClick(track: Track) {
        if (viewModel.clickDebounce()) {
            val trackJson = Gson().toJson(track)
            findNavController().navigate(
                R.id.action_mediaFragment_to_playerFragment,
                PlayerFragment.createArgs(trackJson)
            )
        }
    }

    override fun onItemLongClick(track: Track): Boolean {
        return true
    }

    companion object {
        fun newInstance() = MediaFragmentFavoriteTracks()
    }
}