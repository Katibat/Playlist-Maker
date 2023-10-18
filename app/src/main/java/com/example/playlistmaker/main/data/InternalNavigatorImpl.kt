package com.example.playlistmaker.main.data

import android.content.Context
import android.content.Intent
import com.example.playlistmaker.main.domain.api.InternalNavigator
import com.example.playlistmaker.media.ui.MediaActivity
import com.example.playlistmaker.search.ui.SearchActivity
import com.example.playlistmaker.settings.ui.SettingsActivity

class InternalNavigatorImpl(private val context: Context) : InternalNavigator {
    override fun openSearchScreen() {
        context.startActivity(Intent(context, SearchActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    override fun openMediaScreen() {
        context.startActivity(Intent(context, MediaActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    override fun openSettingScreen() {
        context.startActivity(Intent(context, SettingsActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}