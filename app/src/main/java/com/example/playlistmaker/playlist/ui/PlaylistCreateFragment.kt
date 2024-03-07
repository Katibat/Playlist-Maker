package com.example.playlistmaker.playlist.ui

import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
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
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.MediaFragmentCreatePlaylistBinding
import com.example.playlistmaker.playlist.domain.api.PlaylistImageStorage
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.FileOutputStream

class PlaylistCreateFragment : Fragment() {
    private var _binding: MediaFragmentCreatePlaylistBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<PlaylistCreateViewModel>()
    private var isImageSelected = false
    private var urlImageForNewPlaylist: String? = null
    private var backNavigationListenerRoot: BackNavigationListenerRoot? = null

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

        setupListeners()
        setupTextChangeListener()
        setupViewModelObservers()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    navigateBack()
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDetach() {
        super.onDetach()
        backNavigationListenerRoot = null
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
        binding.buttonCreatePlaylist.setOnClickListener { createNewPlaylist() }
    }

    private fun setupViewModelObservers() {
        viewModel.observeImageUrl().observe(viewLifecycleOwner, Observer { url ->
            urlImageForNewPlaylist = url
        })
    }

    private fun handleSelectedImage(uri: Uri) {
        binding.ivImagePlayer.setImageURI(uri)
        binding.icAddImage.isVisible = false
        isImageSelected = true
        saveImageToPrivateStorage(uri)
    }

    private fun createNewPlaylist() {
        val playlistName = binding.playlistName.text.toString()
        viewModel.renameImageFile(playlistName)
        lifecycleScope.launch {
            viewModel.createNewPlaylist(
                playlistName,
                binding.playlistDescription.text.toString(),
                urlImageForNewPlaylist,
                null,
                0
            )
            viewModel.getImageUrlFromStorage(playlistName)
            showToastPlaylistCreated(playlistName)
            findNavController().navigateUp()
        }
    }

    private fun chooseAndUploadImage() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun showToastPlaylistCreated(playlistName: String) {
        val message = getString(R.string.playlist_created, playlistName)
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun saveImageToPrivateStorage(uri: Uri) {
        val file = PlaylistImageStorage.getTemporaryImageFile(requireContext())
        val inputStream = requireActivity().contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory.decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
    }

    fun navigateBack() {
        if (binding.playlistName.text!!.isNotEmpty() ||
            binding.playlistDescription.text!!.isNotEmpty() ||
            isImageSelected) {
            showBackConfirmationDialog()
        } else {
            backNavigationListenerRoot?.onNavigateBack(true)
        }
    }

    private fun showBackConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(context?.getString(R.string.dialog_complete_creation))
            .setMessage(context?.getString(R.string.dialog_data_will_be_lost))
            .setNeutralButton(context?.getString(R.string.dialog_cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.dialog_complete)) { _, _ ->
                findNavController().navigateUp()
            }
            .show()
            .apply {
                getButton(DialogInterface.BUTTON_POSITIVE)
                    .setTextColor(Color.parseColor("#3772E7"))
                getButton(DialogInterface.BUTTON_NEUTRAL)
                    .setTextColor(Color.parseColor("#3772E7"))
            }
    }
}