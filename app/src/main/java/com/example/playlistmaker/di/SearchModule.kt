package com.example.playlistmaker.di

import com.example.playlistmaker.search.data.SearchHistoryStorage
import com.example.playlistmaker.search.data.SearchHistoryStorageImpl
import com.example.playlistmaker.search.data.SearchRepositoryImpl
import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient.Companion.IMDB_BASE_URL
import com.example.playlistmaker.search.data.network.TracksApi
import com.example.playlistmaker.search.domain.api.SearchInteractor
import com.example.playlistmaker.search.domain.api.SearchRepository
import com.example.playlistmaker.search.domain.impl.SearchInteractorImpl
import com.example.playlistmaker.search.ui.SearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val searchModule = module {

    single<TracksApi> {
        Retrofit.Builder().baseUrl(IMDB_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TracksApi::class.java)
    }

    single<NetworkClient> {
        RetrofitNetworkClient(tracksApi = get())
    }

    single<SearchHistoryStorage> {
        SearchHistoryStorageImpl(sharedPreferences = get())
    }

    single<SearchRepository> {
        SearchRepositoryImpl(client = get(), storage = get())
    }

    single<SearchInteractor> {
        SearchInteractorImpl(repository = get())
    }

    viewModel {
        SearchViewModel(
            get()
        )
    }
}