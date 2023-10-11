package com.example.playlistmaker.creator

import android.content.Context
import com.example.playlistmaker.player.data.PlayerRepositoryImpl
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.api.TracksInteractor
import com.example.playlistmaker.player.domain.api.TracksRepository
import com.example.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.example.playlistmaker.player.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.data.network.TracksRepositoryImpl
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.settings.data.SettingsSwitchStorage
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.sharing.domain.impl.SharingInteractorImpl

object Creator {
    private fun getTracksRepository(context: Context): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient(context))
    }

    fun provideTracksInteractor(context: Context): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository(context))
    }

    fun providePlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl(PlayerRepositoryImpl())
    }

    fun provideSettingsInteractor(context: Context): SettingsInteractor {
        val sharedPreferences = context.getSharedPreferences(
            SettingsSwitchStorage.DARK_THEME, Context.MODE_PRIVATE)
        return SettingsInteractorImpl(SettingsRepositoryImpl(
                SettingsSwitchStorage(sharedPreferences)))
    }

    fun provideSharingInteractor(context: Context): SharingInteractor {
        return SharingInteractorImpl(ExternalNavigatorImpl(context))
    }
}