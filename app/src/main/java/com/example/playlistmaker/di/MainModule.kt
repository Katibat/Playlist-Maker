package com.example.playlistmaker.di

import com.example.playlistmaker.main.data.InternalNavigatorImpl
import com.example.playlistmaker.main.domain.api.InternalNavigator
import com.example.playlistmaker.main.domain.api.MainInteractor
import com.example.playlistmaker.main.domain.impl.MainInteractorImp
import com.example.playlistmaker.main.ui.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mainModule = module {

    single<InternalNavigator> {
        InternalNavigatorImpl(context = get())
    }

    single<MainInteractor> {
        MainInteractorImp(internalNavigator = get())
    }

    viewModel {
        MainViewModel(get())
    }
}