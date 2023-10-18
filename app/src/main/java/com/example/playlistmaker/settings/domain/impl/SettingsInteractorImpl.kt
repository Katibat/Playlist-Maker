package com.example.playlistmaker.settings.domain.impl

import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.settings.domain.models.ThemeSwitchDate

class SettingsInteractorImpl(private val repository: SettingsRepository) : SettingsInteractor {
    override fun changeThemeSetting(theme: ThemeSwitchDate) {
        repository.saveThemeSwitchSettings(theme)
    }

    override fun getThemeSettings(): ThemeSwitchDate {
        return repository.getThemeSwitchSettings()
    }
}