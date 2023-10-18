package com.example.playlistmaker.settings.domain.api

import com.example.playlistmaker.settings.domain.models.ThemeSwitchDate

interface SettingsRepository {
    fun saveThemeSwitchSettings(switchDate: ThemeSwitchDate)
    fun getThemeSwitchSettings(): ThemeSwitchDate
}