package com.example.playlistmaker.di

import android.media.MediaPlayer
import com.example.playlistmaker.player.data.PlayerRepositoryImpl
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.api.PlayerRepository
import com.example.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.example.playlistmaker.player.ui.PlayerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playerModule = module {

    single<MediaPlayer> {
        MediaPlayer()
    }

    single<PlayerRepository> {
        PlayerRepositoryImpl()
    }

    single<PlayerInteractor> {
        PlayerInteractorImpl(repository = get())
    }

    viewModel {
        PlayerViewModel(get())
    }
}