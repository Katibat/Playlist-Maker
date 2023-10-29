package com.example.playlistmaker.settings.data

import android.content.SharedPreferences
import com.example.playlistmaker.settings.domain.models.ThemeSwitchDate

class SettingsSwitchStorageImpl
    (private val sharedPreferences: SharedPreferences) : SettingsSwitchStorage {

    override fun saveThemeSwitch(theme: ThemeSwitchDate) {
        sharedPreferences.edit().putBoolean(DARK_THEME, theme.switchTheme).apply()
    }

    override fun getThemeSwitch(): ThemeSwitchDate {
        return ThemeSwitchDate(sharedPreferences.getBoolean(DARK_THEME, false))
    }

    companion object {
        const val DARK_THEME = "dark_theme"
    }
}