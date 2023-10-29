package com.example.playlistmaker.di

import com.example.playlistmaker.sharing.data.NavigatorSharingImpl
import com.example.playlistmaker.sharing.domain.api.NavigatorSharing
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import org.koin.dsl.module

val sharingModule = module {

    single<NavigatorSharing> {
        NavigatorSharingImpl(context = get())
    }

    single<SharingInteractor> {
        SharingInteractorImpl(navigator = get())
    }
}