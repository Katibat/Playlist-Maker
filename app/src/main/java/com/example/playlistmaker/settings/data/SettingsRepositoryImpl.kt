package com.example.playlistmaker.settings.data

import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.settings.domain.models.ThemeSwitchDate

class SettingsRepositoryImpl(private val storage: SettingsSwitchStorage) : SettingsRepository {
    override fun saveThemeSwitchSettings(switchDate: ThemeSwitchDate) {
        storage.saveThemeSwitch(switchDate)
    }

    override fun getThemeSwitchSettings(): ThemeSwitchDate {
        return storage.getThemeSwitch()
    }
}