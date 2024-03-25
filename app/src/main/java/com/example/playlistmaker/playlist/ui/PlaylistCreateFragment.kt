package com.example.playlistmaker.playlist.ui

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.MediaFragmentCreatePlaylistBinding
import com.example.playlistmaker.playlist.domain.models.Playlist
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistCreateFragment : Fragment() {
    private var _binding: MediaFragmentCreatePlaylistBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<PlaylistCreateViewModel>()
    private var isImageSelected = false
    private var urlImageForPlaylist: String? = null
    private var editablePlaylist: Playlist? = null

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let {
                handleSelectedImage(it)
            } ?: run {}
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MediaFragmentCreatePlaylistBinding.inflate(inflater, container, false)
        editablePlaylist = arguments?.getSerializable("EDIT_PLAYLIST") as? Playlist

        setupListeners()
        setupTextChangeListener()
        setupViewModelObservers()
        editablePlaylist?.let { setupUiEditMode(it) }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    navigateBack()
                }
            })

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupTextChangeListener() {
        binding.playlistName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {

                binding.buttonCreatePlaylist.isEnabled = s?.isNotEmpty() == true
            }
        })
    }

    private fun setupListeners() {
        binding.ivImagePlayer.setOnClickListener { chooseAndUploadImage() }
        binding.buttonCreatePlaylist.setOnClickListener {
            if (editablePlaylist == null) {
                createNewPlaylist()
            } else {
                lifecycleScope.launch {
                    editPlaylist(editablePlaylist!!)
                }
            }
        }
    }

    private fun setupViewModelObservers() {
        viewModel.imagePathLiveData.observe(viewLifecycleOwner) { url ->
            urlImageForPlaylist = url
        }
    }

    private fun setupUiEditMode(playlist: Playlist) {
        requireActivity().title = "Редактировать" // getString(R.string.playlist_details_edit)
        binding.playlistName.setText(playlist.name)
        binding.playlistDescription.setText(playlist.description)
        binding.buttonCreatePlaylist.text = "Сохранить" // getString(R.string.playlist_details_save)
        if (playlist.imageUrl != null) {
            Glide.with(this)
                .load(playlist.imageUrl)
                .centerCrop()
                .into(binding.ivImagePlayer)
            binding.icAddImage.isVisible = false
        } else {
            binding.ivImagePlayer.isVisible = true
            binding.icAddImage.isVisible = true
        }
    }

    private fun handleSelectedImage(uri: Uri) {
        binding.ivImagePlayer.setImageURI(uri)
        binding.icAddImage.isVisible = false
        isImageSelected = true
        viewModel.saveImage(uri)
    }

    private fun createNewPlaylist() {
        val playlistName = binding.playlistName.text.toString()
        viewModel.renameImageFile(playlistName)
        if (isImageSelected) {
            lifecycleScope.launch {
                viewModel.createNewPlaylist(
                    playlistName,
                    binding.playlistDescription.text.toString(),
                    urlImageForPlaylist,
                    null,
                    0
                )
            }
        } else {
            lifecycleScope.launch {
                viewModel.createNewPlaylist(
                    playlistName,
                    binding.playlistDescription.text.toString(),
                    null,
                    null,
                    0
                )
            }
        }
        showToastPlaylistCreated(playlistName)
        findNavController().navigateUp()
    }

    private suspend fun editPlaylist(playlist: Playlist) {
        val updatedName = binding.playlistName.text.toString()
        val updatedDescription = binding.playlistDescription.text.toString()
        val updatedTracksIds = if (playlist.tracksIds?.isEmpty() == true) null else playlist.tracksIds
        val updatedCountTracks = if (playlist.countTracks == 0) null else playlist.countTracks
        val updatedPlaylist: Playlist?
        if (isImageSelected) {
            updatedPlaylist = playlist.copy(
                name = updatedName,
                description = updatedDescription,
                imageUrl = urlImageForPlaylist,
                tracksIds = updatedTracksIds,
                countTracks = updatedCountTracks)
            lifecycleScope.launch {
                viewModel.editPlaylist(updatedPlaylist)
            }
        } else {
            updatedPlaylist = playlist.copy(
                name = updatedName,
                description = updatedDescription,
                imageUrl = urlImageForPlaylist,
                tracksIds = updatedTracksIds,
                countTracks = updatedCountTracks)
            lifecycleScope.launch {
                viewModel.editPlaylist(updatedPlaylist)
            }
        }
        val bundle = Bundle()
        bundle.putSerializable("playlist", updatedPlaylist)
        parentFragmentManager.setFragmentResult("playlist", bundle)
        findNavController().navigateUp()
    }

    private fun chooseAndUploadImage() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun showToastPlaylistCreated(playlistName: String) {
        val message = getString(R.string.playlist_created, playlistName)
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    fun navigateBack() {
        if (binding.playlistName.text!!.isNotEmpty() ||
            binding.playlistDescription.text!!.isNotEmpty() ||
            isImageSelected
        ) {
            showBackConfirmationDialog()
        } else {
            findNavController().navigateUp()
        }
    }

    private fun showBackConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext(), R.style.DialogStyle)
            .setTitle(context?.getString(R.string.dialog_complete_creation))
            .setMessage(context?.getString(R.string.dialog_data_will_be_lost))
            .setNeutralButton(context?.getString(R.string.dialog_cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.dialog_complete)) { dialog, _ ->
                findNavController().navigateUp()
                dialog.dismiss() // иначе остается диалог на экране медиатеки
            }
            .show()
            .apply {
                getButton(DialogInterface.BUTTON_POSITIVE)
                    .setTextColor(resources.getColor(R.color.progressBar_tint, null))
                getButton(DialogInterface.BUTTON_NEUTRAL)
                    .setTextColor(resources.getColor(R.color.progressBar_tint, null))
            }
    }

    companion object {
        fun createArgs(id: Int?, name: String?, description: String?, imageUrl: String?): Bundle? =
            bundleOf(
                "PLAYLIST_ID" to id,
                "PLAYLIST_NAME" to name,
                "DESCRIPTION" to description,
                "PREVIEW" to imageUrl
            )
    }
}