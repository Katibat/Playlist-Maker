package com.example.playlistmaker.di

import com.example.playlistmaker.main.data.NavigatorMainImpl
import com.example.playlistmaker.main.domain.api.NavigatorMain
import com.example.playlistmaker.main.ui.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mainModule = module {

    single<NavigatorMain> {
        NavigatorMainImpl(context = get())
    }

    viewModel {
        MainViewModel(get())
    }
}