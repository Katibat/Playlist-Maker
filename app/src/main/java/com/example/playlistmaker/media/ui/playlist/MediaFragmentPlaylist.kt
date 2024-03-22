package com.example.playlistmaker.media.ui.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.MediaFragmentPlaylistsBinding
import com.example.playlistmaker.playlist.domain.models.Playlist
import com.example.playlistmaker.media.util.PlaylistState
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class MediaFragmentPlaylist : Fragment() {
    private var _binding: MediaFragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by activityViewModel<MediaPlaylistViewModel>()

    private var adapter: PlaylistsAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MediaFragmentPlaylistsBinding.inflate(inflater, container, false)
        adapter = PlaylistsAdapter(mutableListOf(), object : PlaylistsAdapter.Listener {
            override fun onClick(playlist: Playlist) {
                val bundle = Bundle()
                bundle.putSerializable("playlist", playlist)
                findNavController().navigate(R.id.playlistDetailsFragment, bundle)
            }
        })

        binding.rvPlaylists.adapter = adapter

        binding.buttonCreateNewPlaylist.setOnClickListener {
            findNavController().navigate(R.id.playlistCreateFragment, null)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = GridLayoutManager(context, 2)
        binding.rvPlaylists.layoutManager = layoutManager

        viewModel.liveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                is PlaylistState.Content -> showContent(state.playlist)
                is PlaylistState.Empty -> showEmpty()
                is PlaylistState.Loading -> {}
                else -> {}
            }
        }

        viewModel.fillData()
    }

    private fun showEmpty() {
        binding.rvPlaylists.isVisible = false
        binding.progressBar.isVisible = false
        binding.ivNotFound.isVisible = true
        binding.tvNotFound.isVisible = true
    }

    private fun showContent(playlists: List<Playlist>) {
        binding.rvPlaylists.isVisible = true
        binding.progressBar.isVisible = false
        binding.ivNotFound.isVisible = false
        binding.tvNotFound.isVisible = false

        adapter?.updateData(playlists)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = MediaFragmentPlaylist()
    }
}