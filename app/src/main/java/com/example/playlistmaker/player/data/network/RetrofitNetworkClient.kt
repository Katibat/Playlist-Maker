package com.example.playlistmaker.player.data.network

import com.example.playlistmaker.player.data.NetworkClient
import com.example.playlistmaker.player.data.dto.Response
import com.example.playlistmaker.player.data.dto.TracksRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient : NetworkClient {

    private val imdbBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(imdbBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val imdbService = retrofit.create(TracksApi::class.java)

    override fun doRequest(dto: Any): Response {
        if (dto is TracksRequest) {
            val resp = imdbService.search(dto.expression).execute()

            val body = resp.body() ?: Response()

            return body.apply { resultCode = resp.code() }
        } else {
            return Response().apply { resultCode = 400 }
        }
    }
}