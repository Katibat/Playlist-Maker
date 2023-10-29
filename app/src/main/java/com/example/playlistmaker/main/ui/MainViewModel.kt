package com.example.playlistmaker.main.ui

import androidx.lifecycle.ViewModel
import com.example.playlistmaker.main.domain.api.NavigatorMain

class MainViewModel(val navigator: NavigatorMain) : ViewModel() {
    fun openSearchScreen() {
        navigator.openSearchScreen()
    }

    fun openMediaScreen() {
        navigator.openMediaScreen()
    }

    fun openSettingScreen() {
        navigator.openSettingScreen()
    }
}