package com.example.playlistmaker.search.data

import android.content.SharedPreferences
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson

class SearchHistoryStorage(private val sharedPreferences: SharedPreferences) {

    fun addTrackInHistoryList(tracksList: List<Track>) {
        val json = Gson().toJson(tracksList)
        sharedPreferences.edit()
            .putString(HISTORY, json)
            .apply()
    }

    fun getHistory(): List<Track> {
        val getShared = sharedPreferences.getString(HISTORY, null)
        return if (getShared.isNullOrEmpty()) {
            emptyList()
        } else {
            Gson().fromJson(getShared, Array<Track>::class.java).toList()
        }
    }

    fun clearHistoryList() {
        sharedPreferences.edit().clear().apply()
    }

    companion object {
        const val HISTORY = "history"
    }
}