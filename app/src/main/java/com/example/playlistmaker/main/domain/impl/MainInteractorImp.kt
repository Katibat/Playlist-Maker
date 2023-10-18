package com.example.playlistmaker.main.domain.impl

import com.example.playlistmaker.main.domain.api.InternalNavigator
import com.example.playlistmaker.main.domain.api.MainInteractor

class MainInteractorImp(private val internalNavigator : InternalNavigator) : MainInteractor {
    override fun openSearchScreen() {
        internalNavigator.openSearchScreen()
    }

    override fun openMediaScreen() {
        internalNavigator.openMediaScreen()
    }

    override fun openSettingScreen() {
        internalNavigator.openSettingScreen()
    }
}