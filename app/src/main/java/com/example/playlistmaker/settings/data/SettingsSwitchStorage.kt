package com.example.playlistmaker.settings.data

import android.content.SharedPreferences
import com.example.playlistmaker.settings.domain.models.ThemeSwitchDate

class SettingsSwitchStorage(private val sharedPreferences: SharedPreferences) {
    companion object {
        const val DARK_THEME = "dark_theme"
    }

    fun saveThemeSwitch(theme: ThemeSwitchDate) {
        sharedPreferences.edit().putBoolean(DARK_THEME, theme.switchTheme).apply()
    }

    fun getThemeSwitch(): ThemeSwitchDate {
        return ThemeSwitchDate(sharedPreferences.getBoolean(DARK_THEME, false))
    }
}