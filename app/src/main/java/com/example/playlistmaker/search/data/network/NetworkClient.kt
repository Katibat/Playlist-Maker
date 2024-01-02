package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.player.data.dto.Response

interface NetworkClient {
    suspend fun doRequest(dto: Any): Response
}