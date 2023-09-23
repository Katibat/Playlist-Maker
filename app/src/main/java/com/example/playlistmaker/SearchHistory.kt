package com.example.playlistmaker

import com.example.playlistmaker.player.domain.models.Track
import com.example.playlistmaker.utils.App
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object SearchHistory {
    private const val HISTORY = "history"
    private const val SIZE = 10

    fun getHistory(): ArrayList<Track> {
        var historyList = ArrayList<Track>()
        val getShared = App.sharedPrefs.getString(HISTORY, null)
        if (!getShared.isNullOrEmpty()) {
            val sType = object : TypeToken<ArrayList<Track>>() {}.type
            historyList = Gson().fromJson(getShared, sType)
        }
        return historyList
    }

    fun addTrackInHistoryList(track: Track) {
        val historyList = getHistory()
        if (historyList.contains(track)) {
            historyList.remove(track)
        }
        if (historyList.size >= SIZE) {
            historyList.removeAt(historyList.size - 1)
        }
        historyList.add(0, track)
        createJsonFromTracksList(historyList)
    }

    fun clearHistoryList() {
        createJsonFromTracksList(ArrayList())
    }

    private fun createJsonFromTracksList(tracksList: ArrayList<Track>) {
        val json = Gson().toJson(tracksList)
        App.sharedPrefs.edit()
            .putString(HISTORY, json)
            .apply()
    }
}