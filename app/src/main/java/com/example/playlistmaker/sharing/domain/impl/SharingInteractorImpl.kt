package com.example.playlistmaker.sharing.domain.impl

import com.example.playlistmaker.sharing.domain.api.NavigatorSharing
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.sharing.domain.models.EmailData

class SharingInteractorImpl(private val navigator: NavigatorSharing) : SharingInteractor {

    override fun shareApp() {
        navigator.shareLink(URL_ANDROID_DEVELOPER)
    }

    override fun openTerms() {
        navigator.openLink(URL_OFFER)
    }

    override fun openSupport() {
        navigator.openEmail(EmailData(email = SUPPORT_EMAIL))
    }

    companion object {
        const val URL_ANDROID_DEVELOPER = "https://practicum.yandex.ru/android-developer/"
        const val URL_OFFER = "https://yandex.ru/legal/practicum_offer/"
        const val SUPPORT_EMAIL = "katerina.mahova@yandex.ru"
    }
}