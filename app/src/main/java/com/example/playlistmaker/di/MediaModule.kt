package com.example.playlistmaker.di

import com.example.playlistmaker.favorite.data.FavoriteTracksRepositoryImpl
import com.example.playlistmaker.db.data.TrackDbConvertor
import com.example.playlistmaker.favorite.domain.api.FavoriteTracksInteractor
import com.example.playlistmaker.favorite.domain.impl.FavoriteTracksInteractorImpl
import com.example.playlistmaker.favorite.domain.api.FavoriteTracksRepository
import com.example.playlistmaker.media.ui.favorite.MediaFavoriteTracksViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediaModule = module {

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