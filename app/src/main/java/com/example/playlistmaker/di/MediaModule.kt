package com.example.playlistmaker.di

import com.example.playlistmaker.media.ui.MediaFavoriteTracksViewModel
import com.example.playlistmaker.media.ui.MediaPlaylistsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediaModule = module {

    viewModel {
        MediaFavoriteTracksViewModel()
    }

    viewModel {
        MediaPlaylistsViewModel()
    }
}