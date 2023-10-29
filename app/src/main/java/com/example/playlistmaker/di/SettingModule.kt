package com.example.playlistmaker.di

import android.content.Context
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.settings.data.SettingsSwitchStorage
import com.example.playlistmaker.settings.data.SettingsSwitchStorageImpl
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.settings.ui.SettingsViewModel
import com.example.playlistmaker.utils.App
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val settingsModule = module {

    single {
        androidContext()
            .getSharedPreferences(
                "local_storage", Context.MODE_PRIVATE
            )
    }

    single<SettingsSwitchStorage> {
        SettingsSwitchStorageImpl(sharedPreferences = get())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(storage = get())
    }

    single<SettingsInteractor> {
        SettingsInteractorImpl(repository = get())
    }

    viewModel {
        SettingsViewModel(
            settingsInteractor = get(),
            sharingInteractor = get(),
            androidApplication() as App
        )
    }
}