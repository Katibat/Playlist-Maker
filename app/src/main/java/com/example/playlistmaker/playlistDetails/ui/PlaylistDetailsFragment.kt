package com.example.playlistmaker.playlistDetails.ui

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.MediaFragmentPlaylistDetailsBinding
import com.example.playlistmaker.player.ui.PlayerFragment
import com.example.playlistmaker.playlist.domain.models.Playlist
import com.example.playlistmaker.playlist.ui.PlaylistCreateFragment
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.TrackAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistDetailsFragment : Fragment() {
    private val viewModel: PlaylistDetailsViewModel by activityViewModel()
    private var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>? = null
    private var playlist: Playlist? = null
    private var editPlaylist: Playlist? = null
    private var playlistToEdit: Playlist? = null
    private var adapter: TrackAdapter? = null

    private var _binding: MediaFragmentPlaylistDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MediaFragmentPlaylistDetailsBinding.inflate(inflater, container, false)
        playlist = arguments?.getSerializable("playlist") as? Playlist
//        viewModel.getPlaylistById(requireArguments().getInt("ARGS_PLAYLIST"))
        adapter = TrackAdapter {
            findNavController().navigate(
                R.id.action_playlistDetailsFragment_to_playerFragment,
                PlayerFragment.createArgs(Gson().toJson(it))
            )
        }
        adapter!!.onLongTrackClick = { track ->
            onItemLongClick(track)
            true
        }
        binding.recycleViewBottomSheet.adapter = adapter

        if (editPlaylist == null || playlist?.id != editPlaylist?.id) {
            viewModel.getPlaylistById(playlist!!.id)
            viewModel.getTracksFromCurrentPlaylist(playlist!!)
            editPlaylist = playlist!!.copy()
            playlistToEdit = playlist!!.copy()
        } else {
            viewModel.getPlaylistById(editPlaylist!!.id)
            viewModel.getTracksFromCurrentPlaylist(editPlaylist!!)
            playlistToEdit = editPlaylist!!.copy()
        }
        val bottomSheetContainer = binding.standardBottomSheetMenuDetails
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer)
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupObservers()
        setUpClickListeners()
        setupBottomSheetBehaviorCallback()
    }

    private fun setupUI() {
        binding.recycleViewBottomSheet.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupObservers() {
        viewModel.playlistDetails.observe(viewLifecycleOwner) { playlist ->
            updateUi(playlist)
            playlistToEdit!!.name = playlist.name
            playlistToEdit!!.description = playlist.description
            playlistToEdit!!.imageUrl = playlist.imageUrl
        }
        viewModel.tracksLiveData.observe(viewLifecycleOwner) { tracks ->
            val ids = tracks?.map { it.trackId }
            val totalTrackTimeMillis = tracks?.sumBy { it.trackTimeMillis.toInt() }
            editPlaylist = editPlaylist!!.copy(tracksIds = ids)
            showContent(tracks, totalTrackTimeMillis)
            playlistToEdit!!.tracksIds = ids
            playlistToEdit!!.countTracks = tracks?.size
        }
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun showContent(tracks: List<Track>?, duration: Int?) {
        if (tracks.isNullOrEmpty()) {
            binding.apply {
                messageEmptyList.isVisible = true
                recycleViewBottomSheet.isVisible = false
                playlistMinutes.text = "0 минут"
                playlistTracks.text = "0 треков"
            }
        }
        if (!tracks.isNullOrEmpty()) {
            binding.messageEmptyList.visibility = View.GONE
            binding.recycleViewBottomSheet.visibility = View.VISIBLE
            val numberOfTracks = tracks.size
            val updatedTracks = tracks.map { track ->
                track.copy(
                    artworkUrl100 = track.artworkUrl100
                        .replaceAfterLast('/', "60x60bb.jpg")
                )
            }

            with(binding) {
                val formattedDuration = getFormattedDuration(duration)
                adapter?.tracksList = ArrayList(updatedTracks)
                recycleViewBottomSheet.adapter = adapter
                adapter?.notifyDataSetChanged()
                recycleViewBottomSheet.visibility = View.VISIBLE
                playlistMinutes.text =
                    "$formattedDuration ${getMinuteWordForm(formattedDuration)}"
                playlistTracks.text = "$numberOfTracks ${getTrackWordForm(numberOfTracks)}"
            }
        }
    }

    private fun setUpClickListeners() {
        binding.iconShare.setOnClickListener {
            if (editPlaylist?.countTracks == 0) {
                Toast.makeText(
                    requireContext(),
                    context?.getString(R.string.no_tracks_to_share),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                adapter?.tracksList?.let { it1 -> viewModel.shareTracks(playlist!!, it1) }
            }
        }

        binding.iconMenu.setOnClickListener {
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
            binding.dimOverlay.visibility = View.VISIBLE
        }

        binding.sharePlaylist.setOnClickListener {
            if (editPlaylist?.countTracks == 0) {
                Toast.makeText(
                    requireContext(),
                    context?.getString(R.string.no_tracks_to_share),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                viewModel.shareTracks(playlist!!, adapter?.tracksList)
            }
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.deletePlaylist.setOnClickListener {
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
            MaterialAlertDialogBuilder(requireContext(), R.style.DialogStyle)
                .setTitle(context?.getString(R.string.dialog_delete_playlist_title))
                .setMessage(context?.getString(R.string.dialog_delete_playlist_message))
                .setNeutralButton(context?.getString(R.string.dialog_delete_playlist_cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(getString(R.string.dialog_delete_playlist_delete)) { _, _ ->
                    lifecycleScope.launch {
                        viewModel.deletePlaylistById(playlist!!.id, adapter?.tracksList!!)
                        findNavController().popBackStack()
                    }
                }
                .setOnDismissListener {
                }
                .show()
                .apply {
                    getButton(DialogInterface.BUTTON_POSITIVE)
                        .setTextColor(resources.getColor(R.color.progressBar_tint, null))
                    getButton(DialogInterface.BUTTON_NEUTRAL)
                        .setTextColor(resources.getColor(R.color.progressBar_tint, null))
                }
        }

        binding.editPlaylist.setOnClickListener {
            viewModel.getPlaylistById(editPlaylist!!.id)
//            val playlist: Playlist? = playlistToEdit
//            val bundle = Bundle().apply {
//                putSerializable("EDIT_PLAYLIST", playlist)
//            }
//            findNavController().navigate(R.id.mediaFragmentPlaylist, bundle)
            findNavController()
                .navigate(
                    R.id.action_playlistDetailsFragment_to_playlistCreateFragment,
                    PlaylistCreateFragment.createArgs(
                        editPlaylist!!.id,
                        editPlaylist!!.name,
                        editPlaylist!!.description,
                        editPlaylist!!.imageUrl))
        }
    }

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

    private fun setupBottomSheet(playlist: Playlist) {
        binding.recycleViewBottomSheet.layoutManager = LinearLayoutManager(requireContext())
        binding.playlistNameBtnSheet.text = playlist.name
        binding.numberOfTracksBtnSheet.text =
            "${playlist.countTracks.toString()} ${getTrackWordForm(playlist.countTracks ?: 0)}"
        Glide.with(requireContext())
            .load(playlist.imageUrl)
            .placeholder(R.drawable.placeholder)
            .into(binding.playlistCoverImageBtnSheet)
    }

    private fun updateUi(playlist: Playlist) {
        setupBottomSheet(playlist)
        with(binding) {
            playlistName.text = playlist.name
            playlistDetails.text = playlist.description
            playlistTracks.text =
                "${playlist.countTracks.toString()} ${getTrackWordForm(playlist.countTracks ?: 0)}"
            Glide.with(root.context)
                .load(playlist.imageUrl)
                .placeholder(R.drawable.placeholder)
                .into(imagePlaylistCover)
        }
        binding.playlistDetails.isVisible = playlist.description?.isEmpty() != true
    }

    fun onItemLongClick(track: Track): Boolean {
        MaterialAlertDialogBuilder(requireContext(), R.style.DialogStyle)
            .setTitle(context?.getString(R.string.dialog_delete_title))
            .setMessage(context?.getString(R.string.dialog_delete_message))
            .setNeutralButton(context?.getString(R.string.dialog_delete_cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.dialog_delete_delete)) { _, _ ->
                lifecycleScope.launch {
                    viewModel.deleteTrackFromPlaylist(track, editPlaylist!!)
                }
            }
            .setOnDismissListener {
            }
            .show()
            .apply {
                getButton(DialogInterface.BUTTON_POSITIVE)
                    .setTextColor(resources.getColor(R.color.progressBar_tint, null))
                getButton(DialogInterface.BUTTON_NEUTRAL)
                    .setTextColor(resources.getColor(R.color.progressBar_tint, null))
            }
        return true
    }

    private fun getTrackWordForm(count: Int): String {
        return when {
            count % 100 in 11..14 -> "треков"
            count % 10 == 1 -> "трек"
            count % 10 in 2..4 -> "трека"
            else -> "треков"
        }
    }

    private fun getMinuteWordForm(count: Int): String {
        return when {
            count % 100 in 11..14 -> "минут"
            count % 10 == 1 -> "минута"
            count % 10 in 2..4 -> "минуты"
            else -> "минут"
        }
    }

    private fun getFormattedDuration(duration: Int?): Int {
        return if (duration !== null) {
            SimpleDateFormat("mm", Locale.getDefault()).format(duration).toInt()
        } else {
            0
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

//    companion object {
//        fun createArgs(playlistId: Int): Bundle =
//            bundleOf("ARGS_PLAYLIST" to playlistId)
//
//    }
}