package com.example.playlistmaker.settings.domain.api

import com.example.playlistmaker.settings.domain.models.ThemeSwitchDate

interface SettingsInteractor {
    fun changeThemeSetting(theme: ThemeSwitchDate)
    fun getThemeSettings(): ThemeSwitchDate
}