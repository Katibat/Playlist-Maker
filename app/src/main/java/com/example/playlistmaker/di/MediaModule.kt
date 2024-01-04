package com.example.playlistmaker.di

import com.example.playlistmaker.db.data.FavoriteTracksRepositoryImpl
import com.example.playlistmaker.db.data.TrackDbConvertor
import com.example.playlistmaker.db.domain.FavoriteTracksInteractor
import com.example.playlistmaker.db.domain.FavoriteTracksInteractorImpl
import com.example.playlistmaker.db.domain.FavoriteTracksRepository
import com.example.playlistmaker.db.ui.FavoriteTrackViewModel
import com.example.playlistmaker.media.ui.MediaFavoriteTracksViewModel
import com.example.playlistmaker.media.ui.MediaPlaylistsViewModel
import org.koin.android.ext.koin.androidContext
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
        FavoriteTrackViewModel(get())
    }
}