package com.example.playlistmaker.settings.data

import com.example.playlistmaker.settings.domain.models.ThemeSwitchDate

interface SettingsSwitchStorage {
    fun saveThemeSwitch(theme: ThemeSwitchDate)
    fun getThemeSwitch(): ThemeSwitchDate
}