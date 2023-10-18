package com.example.playlistmaker.sharing.domain.impl

import com.example.playlistmaker.sharing.domain.api.ExternalNavigator
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.sharing.domain.models.EmailData

class SharingInteractorImpl(private val externalNavigator: ExternalNavigator) : SharingInteractor {

    override fun shareApp() {
        externalNavigator.shareLink(URL_ANDROID_DEVELOPER)
    }

    override fun openTerms() {
        externalNavigator.openLink(URL_OFFER)
    }

    override fun openSupport() {
        externalNavigator.openEmail(EmailData(email = SUPPORT_EMAIL))
    }

    companion object {
        const val URL_ANDROID_DEVELOPER = "https://practicum.yandex.ru/android-developer/"
        const val URL_OFFER = "https://yandex.ru/legal/practicum_offer/"
        const val SUPPORT_EMAIL = "katerina.mahova@yandex.ru"
    }
}