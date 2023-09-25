package com.example.playlistmaker.player.data.network

import com.example.playlistmaker.player.data.NetworkClient
import com.example.playlistmaker.player.data.dto.Response
import com.example.playlistmaker.player.data.dto.TracksRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient : NetworkClient {

    private val retrofit = Retrofit.Builder()
        .baseUrl(IMDB_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val imdbService = retrofit.create(TracksApi::class.java)

    override fun doRequest(dto: Any): Response {
        if (dto is TracksRequest) {
            val response = imdbService.search(dto.expression).execute()

            val body = response.body() ?: Response()

            return body.apply { resultCode = response.code() }
        } else {
            return Response().apply { resultCode = BAD_REQUEST_HTTP_CODE }
        }
    }

    companion object {
        const val IMDB_BASE_URL = "https://itunes.apple.com"
        private const val BAD_REQUEST_HTTP_CODE = 400
        const val OK_HTTP_CODE = 200
    }
}