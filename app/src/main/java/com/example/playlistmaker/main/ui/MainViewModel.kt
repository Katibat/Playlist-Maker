package com.example.playlistmaker.main.ui

import androidx.lifecycle.ViewModel
import com.example.playlistmaker.main.domain.api.MainInteractor

class MainViewModel(val interactor: MainInteractor) : ViewModel() {
    fun openSearchScreen() {
        interactor.openSearchScreen()
    }

    fun openMediaScreen() {
        interactor.openMediaScreen()
    }

    fun openSettingScreen() {
        interactor.openSettingScreen()
    }
}