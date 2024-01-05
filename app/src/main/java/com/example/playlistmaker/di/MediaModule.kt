package com.example.playlistmaker.di

import com.example.playlistmaker.media.data.FavoriteTracksRepositoryImpl
import com.example.playlistmaker.db.data.TrackDbConvertor
import com.example.playlistmaker.media.domain.api.FavoriteTracksInteractor
import com.example.playlistmaker.media.domain.impl.FavoriteTracksInteractorImpl
import com.example.playlistmaker.media.domain.api.FavoriteTracksRepository
import com.example.playlistmaker.media.ui.favorite.MediaFavoriteTracksViewModel
import com.example.playlistmaker.media.ui.playlist.MediaPlaylistsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediaModule = module {

    viewModel {
        MediaFavoriteTracksViewModel()
    }

    viewModel {
        MediaPlaylistsViewModel()
    }

    factory { TrackDbConvertor() }

    single<FavoriteTracksRepository> {
        FavoriteTracksRepositoryImpl(get(), get())
    }

    single<FavoriteTracksInteractor> {
        FavoriteTracksInteractorImpl(get())
    }

    viewModel {
        MediaFavoriteTracksViewModel(get())
    }
}