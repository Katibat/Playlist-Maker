package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.domain.models.NetworkError
import com.example.playlistmaker.search.domain.models.Track

interface NetworkClient {
    fun doRequest(query: String, onSuccess: (List<Track>) -> Unit, onError: (NetworkError) -> Unit)
}