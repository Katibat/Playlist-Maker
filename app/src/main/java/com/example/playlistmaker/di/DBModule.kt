package com.example.playlistmaker.di

import androidx.room.Room
import com.example.playlistmaker.db.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val DBModule = module {

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "playlist_database.db")
//            .addMigrations(AppDatabase.MIGRATION_1_2)
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }
    single { get<AppDatabase>().trackDao() }
    single { get<AppDatabase>().playlistDao() }
    single { get<AppDatabase>().trackInPlaylistDao() }
}