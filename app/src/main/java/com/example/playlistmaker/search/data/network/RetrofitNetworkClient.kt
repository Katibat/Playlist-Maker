package com.example.playlistmaker.search.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.playlistmaker.player.data.dto.Response
import com.example.playlistmaker.player.data.dto.TracksRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient(private val context: Context) : NetworkClient {

    private val retrofit = Retrofit.Builder()
        .baseUrl(IMDB_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val imdbService = retrofit.create(TracksApi::class.java)

    override fun doRequest(dto: Any): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = -1 }
        }

        return if (dto is TracksRequest) {
            val response = imdbService.search(dto.expression).execute()

            val body = response.body() ?: Response()

            body.apply { resultCode = response.code() }
        } else {
            Response().apply { resultCode = BAD_REQUEST_HTTP_CODE }
        }
    }

    private fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
            }
        }
        return false
    }

    companion object {
        const val IMDB_BASE_URL = "https://itunes.apple.com"
        private const val BAD_REQUEST_HTTP_CODE = 400
        const val OK_HTTP_CODE = 200
    }
}