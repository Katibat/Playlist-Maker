package com.example.playlistmaker.di

import android.content.Context
import com.example.playlistmaker.db.convertor.PlaylistDbConvertor
import com.example.playlistmaker.favorite.data.FavoriteTracksRepositoryImpl
import com.example.playlistmaker.db.convertor.TrackDbConvertor
import com.example.playlistmaker.db.convertor.TrackInPlaylistDbConvertor
import com.example.playlistmaker.favorite.domain.api.FavoriteTracksInteractor
import com.example.playlistmaker.favorite.domain.impl.FavoriteTracksInteractorImpl
import com.example.playlistmaker.favorite.domain.api.FavoriteTracksRepository
import com.example.playlistmaker.media.ui.favorite.MediaFavoriteTracksViewModel
import com.example.playlistmaker.media.ui.playlist.MediaPlaylistViewModel
import com.example.playlistmaker.media.data.PlaylistRepositoryImpl
import com.example.playlistmaker.media.domain.api.PlaylistInteractor
import com.example.playlistmaker.media.domain.api.PlaylistRepository
import com.example.playlistmaker.media.domain.impl.PlaylistInteractorImpl
import com.example.playlistmaker.media.ui.playlist.PlaylistCreateViewModel
import com.example.playlistmaker.media.ui.details.PlaylistDetailsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediaModule = module {

    factory { TrackDbConvertor() }

    factory { PlaylistDbConvertor() }

    single { get<Context>().contentResolver }

    factory { TrackInPlaylistDbConvertor() }

    single<FavoriteTracksRepository> {
        FavoriteTracksRepositoryImpl(get(), get())
    }

    single<PlaylistRepository> {
        PlaylistRepositoryImpl(get(), get(), get(), get())
    }

    single<FavoriteTracksInteractor> {
        FavoriteTracksInteractorImpl(get())
    }

    single<PlaylistInteractor> {
        PlaylistInteractorImpl(get())
    }

    viewModel {
        MediaFavoriteTracksViewModel(get())
    }

    viewModel {
        MediaPlaylistViewModel(get())
    }

    viewModel {
        PlaylistCreateViewModel(androidContext(), get())
    }

    viewModel {
        PlaylistDetailsViewModel(get(), get())
    }
}