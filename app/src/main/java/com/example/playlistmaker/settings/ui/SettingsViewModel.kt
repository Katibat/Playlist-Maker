package com.example.playlistmaker.settings.ui

import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.models.ThemeSwitchDate
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.utils.App

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor,
    private val sharingInteractor: SharingInteractor,
    private val application: App) : AndroidViewModel(application) {

    private val themeSwitchSettings = MutableLiveData<ThemeSwitchDate>()
    val themeSettingsState: LiveData<ThemeSwitchDate> = themeSwitchSettings

    init {
        themeSwitchSettings.postValue(settingsInteractor.getThemeSettings())
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        val theme = ThemeSwitchDate(darkThemeEnabled)
        themeSwitchSettings.postValue(theme)
        settingsInteractor.changeThemeSetting(theme)
        application.switchTheme(darkThemeEnabled)
    }

    fun shareApp() {
        sharingInteractor.shareApp()
    }

    fun sendEmailToSupport() {
        sharingInteractor.openSupport()
    }

    fun openUserAgreement() {
        sharingInteractor.openTerms()
    }

    companion object {
        fun getViewModelFactory(context: Context): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val application =
                        this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as App
                    SettingsViewModel(
                        Creator.provideSettingsInteractor(context),
                        Creator.provideSharingInteractor(context),
                        application
                    )
                }
            }
    }
}